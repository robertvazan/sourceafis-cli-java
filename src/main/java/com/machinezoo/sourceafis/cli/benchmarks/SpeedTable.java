// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class SpeedTable {
	private final String key;
	private final PrettyTable table;
	public SpeedTable(String key) {
		this.key = key;
		table = new PrettyTable();
	}
	public void add(String name, TimingData data, TimingData unfiltered) {
		var summary = TimingSeries.summary(data.series());
		double gross = TimingSeries.summary(unfiltered.series()).count() / (double)TimingSeries.duration(unfiltered.series());
		double mean = summary.mean();
		double speed = 1 / mean;
		var sample = Arrays.stream(data.sample()).mapToDouble(m -> m.duration()).sorted().toArray();
		double median = sample.length % 2 == 0
			? 0.5 * (sample[sample.length / 2 - 1] + sample[sample.length / 2])
			: sample[sample.length / 2];
		var sd = Math.sqrt(Arrays.stream(sample).map(v -> Math.pow(v - mean, 2)).sum() / (sample.length - 1));
		var positive = Arrays.stream(sample).filter(v -> v > 0).toArray();
		var gm = Math.exp(Arrays.stream(positive).map(v -> Math.log(v)).sum() / positive.length);
		var gsd = Math.exp(Math.sqrt(Arrays.stream(positive).map(v -> Math.pow(Math.log(v / gm), 2)).sum() / positive.length));
		table.add(key, name);
		table.add("Iterations", Pretty.length(summary.count()));
		table.add("Gross", Pretty.speed(gross));
		table.add("Net", Pretty.speed(speed * data.threads()));
		table.add("Thread", Pretty.speed(speed, name, "thread"));
		table.add("Mean", Pretty.time(mean));
		table.add("Min", Pretty.time(summary.min()));
		table.add("Max", Pretty.time(summary.max()));
		table.add("Sample", Pretty.length(sample.length));
		table.add("Median", Pretty.time(median));
		table.add("SD", Pretty.time(sd));
		table.add("Geom.mean", Pretty.time(gm));
		table.add("GSD", Pretty.factor(gsd));
	}
	public void print() {
		table.print();
	}
}
