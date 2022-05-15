// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class FootprintStats {
	public int count;
	public double serialized;
	public double memory;
	public double minutiae;
	public static FootprintStats sum(List<FootprintStats> list) {
		var sum = new FootprintStats();
		sum.count = Stats.sumAsInt(list, s -> s.count);
		sum.serialized = Stats.sumAsDouble(list, s -> s.serialized);
		sum.memory = Stats.sumAsDouble(list, s -> s.memory);
		sum.minutiae = Stats.sumAsDouble(list, s -> s.minutiae);
		return sum;
	}
}
