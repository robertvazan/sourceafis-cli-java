// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;

interface CacheCompression {
	Path rename(Path path);
	byte[] compress(byte[] data);
	byte[] decompress(byte[] compressed);
	static CacheCompression select(Path path) {
		if (path.getFileName().toString().endsWith(".cbor"))
			return new GzipCompression();
		return new TrivialCompression();
	}
}
