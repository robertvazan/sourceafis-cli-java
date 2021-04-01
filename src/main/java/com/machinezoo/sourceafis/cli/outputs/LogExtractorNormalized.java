// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class LogExtractorNormalized {
	public static byte[] collect(String key, Fingerprint fp) {
		return Cache.get(byte[].class, Paths.get("logs", "extractor", "normalized", key), LogExtractor.identity(key, fp), () -> {
			return Serializer.normalize(ChecksumTransparencyExtractor.row(fp, key).mime, LogExtractor.collect(key, fp));
		});
	}
	public static void collect(String key) {
		for (var fp : Fingerprint.all())
			collect(key, fp);
	}
}
