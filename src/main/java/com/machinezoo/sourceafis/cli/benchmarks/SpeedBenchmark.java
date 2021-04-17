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
	public static final int SAMPLE_SIZE = 10_000;
	public abstract String name();
	protected abstract Dataset dataset(K id);
	protected abstract List<K> shuffle();
	protected abstract TimingStats measure();
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
	protected TimingStats measure(TimedOperation<K> operation) {
		return Cache.get(TimingStats.class, Paths.get("benchmarks", "speed", name()), Paths.get("measurement"), () -> {
			var nondeterministic = new AtomicBoolean(false);
			var epoch = System.nanoTime();
			var strata = parallelize(() -> {
				var ids = shuffle();
				var recorder = new TimingRecorder(epoch, DURATION, SAMPLE_SIZE);
				return () -> {
					while (true) {
						for (var id : ids) {
							operation.prepare(id);
							long start = System.nanoTime();
							operation.execute();
							long end = System.nanoTime();
							if (!operation.verify())
								nondeterministic.set(true);;
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
		var stats = measure();
		var sum = TimingSummary.sum(StreamEx.of(stats.segments.values()).flatArray(a -> a).toList());
		var table = new PrettyTable("Gross");
		table.add(Pretty.speed(sum.count / (double)DURATION, "all", "gross"));
		Pretty.print(table.format());
	}
}
