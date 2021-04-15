// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class ScoreStats {
	public double matching;
	public double nonmatching;
	public double selfmatching;
	public byte[] hash;
	public static ScoreStats sum(List<ScoreStats> list) {
		var sum = new ScoreStats();
		sum.matching = list.stream().mapToDouble(s -> s.matching).average().getAsDouble();
		sum.nonmatching = list.stream().mapToDouble(s -> s.nonmatching).average().getAsDouble();
		sum.selfmatching = list.stream().mapToDouble(s -> s.selfmatching).average().getAsDouble();
		var hash = new Hash();
		for (var stats : list)
			hash.add(stats.hash);
		sum.hash = hash.compute();
		return sum;
	}
}
