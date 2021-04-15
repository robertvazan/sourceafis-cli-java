// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;

interface CacheCompression {
	Path rename(Path path);
	byte[] compress(byte[] data);
	byte[] decompress(byte[] compressed);
}
