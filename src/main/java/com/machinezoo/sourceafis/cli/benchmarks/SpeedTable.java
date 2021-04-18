// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class SpeedTable {
	private final PrettyTable table;
	public SpeedTable(String key) {
		table = new PrettyTable(key, "Iterations", "Parallel", "Thread", "Mean", "Min", "Max", "Sample", "Median", "SD", "Geom.mean", "GSD");
	}
	public void add(String name, TimingStats stats) {
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
			name,
			Pretty.length(total.count),
			Pretty.speed(speed * stats.threads),
			Pretty.speed(speed, name, "thread"),
			Pretty.time(mean),
			Pretty.time(total.min),
			Pretty.time(total.max),
			Pretty.length(sample.length),
			Pretty.time(median),
			Pretty.time(sd),
			Pretty.time(gm),
			Pretty.factor(gsd));
	}
	public void print() {
		Pretty.print(table.format());
	}
}
