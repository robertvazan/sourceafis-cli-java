// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class LogExtractorNormalized {
	private static Path category(String key) {
		return Paths.get("logs", "extractor", "normalized", key);
	}
	private static byte[] collect(String key, Fingerprint fp) {
		return Cache.get(byte[].class, category(key), LogExtractor.identity(key, fp), () -> {
			return Serializer.normalize(ChecksumTransparencyExtractor.row(fp, key).mime, LogExtractor.collect(key, fp));
		});
	}
	public static void collect(String key) {
		for (var fp : Fingerprint.all())
			collect(key, fp);
		Pretty.print("Saved: " + Pretty.dump(category(key)));
	}
}
