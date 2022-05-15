// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils;

import java.util.*;
import java.util.function.*;

public class Stats {
	public static <T> int sumAsInt(Collection<T> list, ToIntFunction<T> getter) {
		return list.stream().mapToInt(getter).sum();
	}
	public static <T> long sumAsLong(Collection<T> list, ToLongFunction<T> getter) {
		return list.stream().mapToLong(getter).sum();
	}
	public static <T> double sumAsDouble(Collection<T> list, ToDoubleFunction<T> getter) {
		return list.stream().mapToDouble(getter).sum();
	}
	public static <T> byte[] sumHash(Collection<T> list, Function<T, byte[]> getter) {
		var hash = new Hasher();
		for (var stats : list)
			hash.add(getter.apply(stats));
		return hash.compute();
	}
	public static <T> double average(Collection<T> list, ToDoubleFunction<T> getter) {
		return list.stream().mapToDouble(getter).average().orElseThrow();
	}
}
