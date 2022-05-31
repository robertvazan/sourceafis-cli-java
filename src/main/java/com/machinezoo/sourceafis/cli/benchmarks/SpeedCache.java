// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;
import one.util.streamex.*;

public interface SpeedCache<K> extends SoloCache<TimingStats> {
	int DURATION = 60;
	int WARMUP = 20;
	int NET_DURATION = DURATION - WARMUP;
	int SAMPLE_SIZE = 10_000;
	String name();
	String description();
	Sampler<K> sampler();
	Supplier<TimedOperation<K>> allocator();
	@Override
	default Path category() {
		return Paths.get("benchmarks", "speed", name());
	}
	@Override
	default Class<TimingStats> type() {
		return TimingStats.class;
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
			threads.add(thread);
		}
		for (var thread : threads)
			thread.start();
		for (var thread : threads)
			Exceptions.sneak().run(() -> thread.join());
		return StreamEx.of(futures).map(CompletableFuture::join).toList();
	}
	@Override
	default TimingStats compute() {
		var epoch = System.nanoTime();
		var allocator = allocator();
		var strata = parallelize(() -> {
			var sampler = sampler();
			var recorder = new TimingRecorder(epoch, DURATION, SAMPLE_SIZE);
			var operation = allocator.get();
			var hasher = new Hasher();
			return () -> {
				while (true) {
					var id = sampler.next();
					operation.prepare(id);
					long start = System.nanoTime();
					operation.execute();
					long end = System.nanoTime();
					operation.blackhole(hasher);
					if (!recorder.record(sampler.dataset(id), start, end))
						return recorder.complete(hasher.compute());
				}
			};
		});
		return TimingStats.sum(SAMPLE_SIZE, strata);
	}
}
