// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
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
		sum.matching = Stats.average(list, s -> s.matching);
		sum.nonmatching = Stats.average(list, s -> s.nonmatching);
		sum.selfmatching = Stats.average(list, s -> s.selfmatching);
		sum.hash = Stats.sumHash(list, s -> s.hash);
		return sum;
	}
}
