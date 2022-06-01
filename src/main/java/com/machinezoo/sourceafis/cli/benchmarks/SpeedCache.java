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

public interface SpeedCache<K> extends SoloCache<TimingData> {
	String name();
	String description();
	Sampler<K> sampler();
	Supplier<TimedOperation<K>> allocator();
	@Override
	default Path category() {
		return Paths.get("benchmarks", "speed", name());
	}
	@Override
	default Class<TimingData> type() {
		return TimingData.class;
	}
	private static List<TimingData> parallelize(Supplier<Supplier<TimingData>> setup) {
		var threads = new ArrayList<Thread>();
		var futures = new ArrayList<CompletableFuture<TimingData>>();
		for (int i = 0; i < Runtime.getRuntime().availableProcessors(); ++i) {
			var future = new CompletableFuture<TimingData>();
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
	default TimingData compute() {
		var epoch = System.nanoTime();
		var allocator = allocator();
		var strata = parallelize(() -> {
			var sampler = sampler();
			var builder = new TimingDataBuilder(epoch);
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
					if (!builder.add(sampler.dataset(id), start, end))
						return builder.build(hasher.compute());
				}
			};
		});
		return TimingData.sum(strata);
	}
}
