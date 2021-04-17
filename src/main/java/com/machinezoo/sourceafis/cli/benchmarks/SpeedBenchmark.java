// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public abstract class SpeedBenchmark implements Runnable {
	public static final int DURATION = 10;
	public static final int SAMPLE_SIZE = 10_000;
	protected abstract TimingStats measure();
	@Override
	public void run() {
		var stats = measure();
		var sum = TimingSummary.sum(StreamEx.of(stats.segments.values()).flatArray(a -> a).toList());
		var table = new PrettyTable("Gross");
		table.add(Pretty.speed(sum.count / sum.sum, "all", "gross"));
		Pretty.print(table.format());
	}
}
