// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;

class TrivialCompression implements CacheCompression {
	@Override
	public Path rename(Path path) {
		return path;
	}
	@Override
	public byte[] compress(byte[] data) {
		return data;
	}
	@Override
	public byte[] decompress(byte[] compressed) {
		return compressed;
	}
}
