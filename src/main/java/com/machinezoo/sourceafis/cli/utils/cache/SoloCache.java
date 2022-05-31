// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;

public interface SoloCache<V> {
	Path category();
	default Path sector() {
		return Paths.get("");
	}
	Class<V> type();
	V compute();
	default V get() {
		return new SoloMapCache<>(this).load().get(null);
	}
}
