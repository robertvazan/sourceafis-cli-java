// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;

public interface FileCache extends MapCache<Path, byte[]> {
	@Override
	default Path identity(Path key) {
		return key;
	}
	@Override
	default Class<byte[]> type() {
		return byte[].class;
	}
}
