// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;

public record FootprintStats(
	int count,
	double serialized,
	double memory,
	double minutiae) {
	public static FootprintStats sum(List<FootprintStats> list) {
		return new FootprintStats(
			Stats.sumAsInt(list, s -> s.count),
			Stats.sumAsDouble(list, s -> s.serialized),
			Stats.sumAsDouble(list, s -> s.memory),
			Stats.sumAsDouble(list, s -> s.minutiae));
	}
}
