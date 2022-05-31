// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;

public record SoloMapCache<V> (SoloCache<V> solo) implements MapCache<Void, V> {
	@Override
	public Path category() {
		return solo.category();
	}
	@Override
	public Path sector() {
		return solo.sector();
	}
	@Override
	public Path identity(Void key) {
		return Paths.get("data");
	}
	@Override
	public Class<V> type() {
		return solo.type();
	}
	@Override
	public void populate(CacheWriter<Void, V> writer) {
		writer.put(null, solo.compute());
	}
}
