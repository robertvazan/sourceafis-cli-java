// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;
import com.machinezoo.sourceafis.cli.config.*;

public interface MapCache<K, V> {
	default Path root() {
		return Configuration.output();
	}
	Path category();
	default Path sector() {
		return Paths.get("");
	}
	Path identity(K key);
	Class<V> type();
	default String action() {
		return "Computing";
	}
	void populate(CacheWriter<K, V> writer);
	default CacheReader<K, V> load() {
		return CacheLoader.load(this);
	}
}
