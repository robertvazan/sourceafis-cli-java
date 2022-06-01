// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;

public record ScoreStats(
	double matching,
	double nonmatching,
	double selfmatching,
	byte[] hash) {
	public static ScoreStats sum(List<ScoreStats> list) {
		return new ScoreStats(
			Stats.average(list, s -> s.matching),
			Stats.average(list, s -> s.nonmatching),
			Stats.average(list, s -> s.selfmatching),
			Stats.hash(list, s -> s.hash));
	}
}
