// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils;

import java.util.concurrent.*;

public class Once<K> {
	private final ConcurrentMap<K, Object> flags = new ConcurrentHashMap<>();
	public boolean first(K key) {
		var flag = new Object();
		return flag == flags.computeIfAbsent(key, k -> flag);
	}
}
