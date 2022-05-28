// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import org.apache.commons.lang3.tuple.*;
import one.util.streamex.*;

public record OperationTiming(String dataset, double start, double end) {
	public static OperationTiming[] sample(int size, List<Pair<TimingSummary, OperationTiming[]>> strata) {
		if (strata.stream().mapToInt(s -> s.getRight().length).sum() <= size)
			return strata.stream().flatMap(s -> Arrays.stream(s.getRight())).toArray(OperationTiming[]::new);
		if (strata.stream().anyMatch(s -> s.getRight().length == 0))
			throw new IllegalArgumentException("Empty sample.");
		var weights = strata.stream().mapToDouble(s -> s.getLeft().count() / (double)s.getRight().length).toArray();
		double total = Arrays.stream(weights).sum();
		for (int i = 0; i < weights.length; ++i)
			weights[i] /= total;
		var available = StreamEx.of(strata).map(s -> new ArrayList<>(List.of(s.getRight()))).toList();
		var sample = new ArrayList<OperationTiming>();
		var random = new Random();
		while (sample.size() < size) {
			var weight = random.nextDouble();
			double cumulative = 0;
			for (int i = 0; i < weights.length; ++i) {
				cumulative += weights[i];
				if (weight < cumulative) {
					var remaining = available.get(i);
					if (!remaining.isEmpty()) {
						int choice = random.nextInt(remaining.size());
						sample.add(remaining.get(choice));
						int last = remaining.size() - 1;
						remaining.set(choice, remaining.get(last));
						remaining.remove(last);
					}
					break;
				}
			}
		}
		return sample.toArray(OperationTiming[]::new);
	}
}
