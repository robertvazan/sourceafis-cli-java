// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;

public class FootprintStats {
	public int count;
	public double serialized;
	public double memory;
	public double minutiae;
	public static FootprintStats sum(List<FootprintStats> list) {
		var sum = new FootprintStats();
		for (var stats : list) {
			sum.count += stats.count;
			sum.serialized += stats.serialized;
			sum.memory += stats.memory;
			sum.minutiae += stats.minutiae;
		}
		return sum;
	}
}
