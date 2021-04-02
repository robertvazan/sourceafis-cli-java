// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class LogExtractor extends LogBase {
	private static Path identity(String key, Fingerprint fp, int index, int count, String mime) {
		return identity(fp.path(), index, count, mime);
	}
	private static Path category(String key) {
		return category(key, "extractor");
	}
	private static byte[] collect(String key, Fingerprint fp, int index, int count) {
		var mime = ChecksumTransparencyExtractor.mime(fp, key);
		return Cache.get(byte[].class, category(key), identity(key, fp, index, count, mime), map -> {
			collect(key, index, count, mime, n -> identity(key, fp, n, count, mime), () -> new FingerprintTemplate(fp.decode()), map);
		});
	}
	public static void collect(String key) {
		for (var fp : Fingerprint.all()) {
			int count = ChecksumTransparencyExtractor.count(fp, key);
			if (count > 0)
				collect(key, fp, 0, count);
		}
		Pretty.print("Saved: " + Pretty.dump(category(key)));
	}
}
