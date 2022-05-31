// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

import java.io.*;
import java.util.zip.*;
import org.apache.commons.io.*;
import com.machinezoo.noexception.*;

class GzipCompression implements CacheCompression {
	@Override
	public String extension() {
		return ".gz";
	}
	@Override
	public byte[] compress(byte[] data) {
		return Exceptions.sneak().get(() -> {
			var buffer = new ByteArrayOutputStream();
			try (var gzip = new GZIPOutputStream(buffer)) {
				gzip.write(data);
			}
			return buffer.toByteArray();
		});
	}
	@Override
	public byte[] decompress(byte[] compressed) {
		return Exceptions.sneak().get(() -> {
			try (var gzip = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
				return IOUtils.toByteArray(gzip);
			}
		});
	}
}
