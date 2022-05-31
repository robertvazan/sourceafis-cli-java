// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;

public interface LoadedCache<K, V> {
	Path directory();
	V get(K key);
}
