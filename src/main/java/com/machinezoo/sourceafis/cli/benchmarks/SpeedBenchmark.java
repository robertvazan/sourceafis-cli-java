// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import org.apache.commons.math3.stat.descriptive.moment.*;
import org.apache.commons.math3.stat.descriptive.rank.*;
import org.slf4j.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;
import one.util.streamex.*;

public abstract class SpeedBenchmark<K> implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(SpeedBenchmark.class);
	public static final int DURATION = 10;
	public static final int WARMUP = 3;
	public static final int NET_DURATION = DURATION - WARMUP;
	public static final int SAMPLE_SIZE = 10_000;
	public abstract String name();
	protected abstract Dataset dataset(K id);
	protected abstract List<K> shuffle();
	protected abstract TimingStats measure();
	protected static <T> List<T> shuffle(List<T> list) {
		var shuffled = new ArrayList<>(list);
		Collections.shuffle(shuffled);
		return shuffled;
	}
	private static List<TimingStats> parallelize(Supplier<Supplier<TimingStats>> setup) {
		var threads = new ArrayList<Thread>();
		var futures = new ArrayList<CompletableFuture<TimingStats>>();
		for (int i = 0; i < Runtime.getRuntime().availableProcessors(); ++i) {
			var future = new CompletableFuture<TimingStats>();
			futures.add(future);
			var benchmark = setup.get();
			var thread = new Thread(() -> {
				try {
					future.complete(benchmark.get());
				} catch (Throwable ex) {
					future.completeExceptionally(ex);
				}
			});
			thread.start();
		}
		for (var thread : threads)
			thread.start();
		for (var thread : threads)
			Exceptions.sneak().run(() -> thread.join());
		return StreamEx.of(futures).map(CompletableFuture::join).toList();
	}
	protected TimingStats measure(Supplier<TimedOperation<K>> setup) {
		return Cache.get(TimingStats.class, Paths.get("benchmarks", "speed", name()), Paths.get("measurement"), () -> {
			var nondeterministic = new AtomicBoolean(false);
			var epoch = System.nanoTime();
			var strata = parallelize(() -> {
				var ids = shuffle();
				var recorder = new TimingRecorder(epoch, DURATION, SAMPLE_SIZE);
				var operation = setup.get();
				return () -> {
					while (true) {
						for (var id : ids) {
							operation.prepare(id);
							long start = System.nanoTime();
							operation.execute();
							long end = System.nanoTime();
							if (!operation.verify())
								nondeterministic.set(true);
							if (!recorder.record(dataset(id), start, end))
								return recorder.complete();
						}
					}
				};
			});
			if (nondeterministic.get())
				logger.warn("Non-deterministic algorithm.");
			return TimingStats.sum(SAMPLE_SIZE, strata);
		});
	}
	@Override
	public void run() {
		var all = measure().skip(WARMUP);
		var global = TimingSummary.sum(StreamEx.of(all.segments.values()).flatArray(a -> a).toList());
		Pretty.print("Gross speed: " + Pretty.speed(global.count / (double)NET_DURATION, "gross"));
		var table = new PrettyTable("Dataset", "Sample", "Parallel", "Thread", "Average", "Min", "Max", "Median", "Stddev");
		for (var profile : Profile.all()) {
			var stats = all.narrow(profile);
			var total = TimingSummary.sum(StreamEx.of(stats.segments.values()).flatArray(a -> a).toList());
			double duration = total.sum / total.count;
			double speed = 1 / duration;
			var sample = Arrays.stream(stats.sample).mapToDouble(o -> o.end - o.start).toArray();
			table.add(
				profile.name,
				Pretty.length(total.count),
				Pretty.speed(speed * all.threads),
				Pretty.speed(speed, profile.name, "thread"),
				Pretty.time(duration),
				Pretty.time(total.min),
				Pretty.time(total.max),
				Pretty.time(new Median().evaluate(sample)),
				Pretty.time(new StandardDeviation().evaluate(sample, duration)));
		}
		Pretty.print(table.format());
	}
}
