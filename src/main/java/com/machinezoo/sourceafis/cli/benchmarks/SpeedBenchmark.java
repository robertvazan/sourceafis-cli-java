// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import org.slf4j.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.cli.datasets.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;
import one.util.streamex.*;

public abstract class SpeedBenchmark<K> extends Command {
	private static final Logger logger = LoggerFactory.getLogger(SpeedBenchmark.class);
	public static final int DURATION = 60;
	public static final int WARMUP = 20;
	public static final int NET_DURATION = DURATION - WARMUP;
	public static final int SAMPLE_SIZE = 10_000;
	public abstract String name();
	protected abstract Dataset dataset(K id);
	protected abstract List<K> shuffle();
	public abstract TimingStats measure();
	@Override
	public List<String> subcommand() {
		return List.of("benchmark", "speed", name());
	}
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
	protected TimingStats measure(Supplier<Supplier<TimedOperation<K>>> setup) {
		return Cache.get(TimingStats.class, Paths.get("benchmarks", "speed", name()), Paths.get("measurement"), () -> {
			var nondeterministic = new AtomicBoolean(false);
			var epoch = System.nanoTime();
			var allocator = setup.get();
			var strata = parallelize(() -> {
				var ids = shuffle();
				var recorder = new TimingRecorder(epoch, DURATION, SAMPLE_SIZE);
				var operation = allocator.get();
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
		var table = new SpeedTable("Dataset");
		for (var profile : Profile.all())
			table.add(profile.name(), all.narrow(profile));
		table.print();
	}
}
