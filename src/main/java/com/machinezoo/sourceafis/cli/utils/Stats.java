// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils;

import java.util.*;
import java.util.function.*;

/*
 * Simplicity in statistics processing is maintained by following a few rules:
 * - Everything is averaged except counts and lengths, which are summed.
 * - Only doubles and longs are used for numeric values.
 * - Hashing is hierarchical: transparency data, transparency row, probe, dataset, profile, global.
 * - Dataset stats are averaged. Only speed benchmarks allow large datasets to dominate results.
 */
public class Stats {
	public static <T> long sum(Collection<T> list, ToLongFunction<T> getter) {
		return list.stream().mapToLong(getter).sum();
	}
	public static <T> double average(Collection<T> list, ToDoubleFunction<T> getter) {
		return list.stream().mapToDouble(getter).average().orElseThrow();
	}
	public static <T> byte[] hash(Collection<T> list, Function<T, byte[]> getter) {
		var hash = new Hasher();
		for (var stats : list)
			hash.add(getter.apply(stats));
		return hash.compute();
	}
}
