// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;

public record FootprintStats(
	double serialized,
	double memory,
	double minutiae) {
	public static FootprintStats sum(List<FootprintStats> list) {
		return new FootprintStats(
			Stats.average(list, s -> s.serialized),
			Stats.average(list, s -> s.memory),
			Stats.average(list, s -> s.minutiae));
	}
}
