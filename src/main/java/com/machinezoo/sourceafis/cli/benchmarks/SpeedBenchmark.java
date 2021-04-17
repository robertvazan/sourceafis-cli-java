// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public abstract class SpeedBenchmark implements Runnable {
	public static final int DURATION = 10;
	public static final int SAMPLE_SIZE = 10_000;
	protected abstract TimingStats measure();
	protected List<TimingStats> parallelize(Supplier<Supplier<TimingStats>> setup) {
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
	@Override
	public void run() {
		var stats = measure();
		var sum = TimingSummary.sum(StreamEx.of(stats.segments.values()).flatArray(a -> a).toList());
		var table = new PrettyTable("Gross");
		table.add(Pretty.speed(sum.count / (double)DURATION, "all", "gross"));
		Pretty.print(table.format());
	}
}
