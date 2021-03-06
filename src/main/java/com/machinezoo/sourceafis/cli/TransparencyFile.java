// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.sourceafis.*;

class TransparencyFile {
	static String extension(String mime) {
		switch (mime) {
		case "application/cbor":
			return ".cbor";
		case "text/plain":
			return ".txt";
		default:
			return ".dat";
		}
	}
	private static class FileCollector extends FingerprintTransparency {
		final String key;
		final List<byte[]> files = new ArrayList<>();
		FileCollector(String key) {
			this.key = key;
		}
		@Override
		public boolean accepts(String key) {
			return this.key.equals(key);
		}
		@Override
		public void take(String key, String mime, byte[] data) {
			files.add(data);
		}
	}
	private static Path extractorPath(String key, SampleFingerprint fp) {
		return PersistentCache.withExtension(fp.path(), extension(TransparencyStats.extractorRow(fp, key).mime));
	}
	static byte[] extractor(String key, SampleFingerprint fp) {
		return PersistentCache.get(byte[].class, Paths.get("extractor-transparency-files", key), extractorPath(key, fp), () -> {
			try (var collector = new FileCollector(key)) {
				new FingerprintTemplate(fp.decode());
				return collector.files.get(0);
			}
		});
	}
	static void extractor(String key) {
		for (var fp : SampleFingerprint.all())
			extractor(key, fp);
	}
	static byte[] extractorNormalized(String key, SampleFingerprint fp) {
		return PersistentCache.get(byte[].class, Paths.get("normalized-extractor-transparency-files", key), extractorPath(key, fp), () -> {
			return SerializationUtils.normalize(TransparencyStats.extractorRow(fp, key).mime, extractor(key, fp));
		});
	}
	static void extractorNormalized(String key) {
		for (var fp : SampleFingerprint.all())
			extractorNormalized(key, fp);
	}
}
