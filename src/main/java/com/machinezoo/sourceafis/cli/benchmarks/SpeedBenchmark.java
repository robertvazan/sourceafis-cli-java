// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
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
		var table = new PrettyTable("Dataset", "Iterations", "Parallel", "Thread", "Mean", "Min", "Max", "Sample", "Median", "SD", "Geom.mean", "GSD");
		for (var profile : Profile.all()) {
			var stats = all.narrow(profile);
			var total = TimingSummary.sum(StreamEx.of(stats.segments.values()).flatArray(a -> a).toList());
			double mean = total.sum / total.count;
			double speed = 1 / mean;
			var sample = Arrays.stream(stats.sample).mapToDouble(o -> o.end - o.start).sorted().toArray();
			double median = sample.length % 2 == 0
				? 0.5 * (sample[sample.length / 2 - 1] + sample[sample.length / 2])
				: sample[sample.length / 2];
			var sd = Math.sqrt(Arrays.stream(sample).map(v -> Math.pow(v - mean, 2)).sum() / (sample.length - 1));
			var positive = Arrays.stream(sample).filter(v -> v > 0).toArray();
			var gm = Math.exp(Arrays.stream(positive).map(v -> Math.log(v)).sum() / positive.length);
			var gsd = Math.exp(Math.sqrt(Arrays.stream(positive).map(v -> Math.pow(Math.log(v / gm), 2)).sum() / positive.length));
			table.add(
				profile.name,
				Pretty.length(total.count),
				Pretty.speed(speed * all.threads),
				Pretty.speed(speed, profile.name, "thread"),
				Pretty.time(mean),
				Pretty.time(total.min),
				Pretty.time(total.max),
				Pretty.length(sample.length),
				Pretty.time(median),
				Pretty.time(sd),
				Pretty.time(gm),
				Pretty.factor(gsd));
		}
		Pretty.print(table.format());
	}
}
