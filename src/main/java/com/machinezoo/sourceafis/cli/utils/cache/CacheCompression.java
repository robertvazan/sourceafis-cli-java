// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;

interface CacheCompression {
	Path rename(Path path);
	byte[] compress(byte[] data);
	byte[] decompress(byte[] compressed);
}
