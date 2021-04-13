// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class LogMatcher extends LogBase {
	private static Path identity(String key, Fingerprint fp, int index, int count, String mime) {
		return identity(fp.path(), index, count, mime);
	}
	private static Path category(String key) {
		return category(key, "matcher");
	}
	private static byte[] collect(String key, Fingerprint fp, int index, int count, String mime) {
		return Cache.get(byte[].class, category(key), identity(key, fp, index, count, mime), map -> {
			var template = Template.of(fp);
			collect(key, index, count, mime, n -> identity(key, fp, n, count, mime), () -> new FingerprintMatcher(template), map);
		});
	}
	public static void collect(String key) {
		var mime = ChecksumTransparencyExtractor.mime(key);
		for (var fp : Fingerprint.all()) {
			int count = ChecksumTransparencyExtractor.count(fp, key);
			if (count > 0)
				collect(key, fp, 0, count, mime);
		}
		Pretty.print("Saved: " + Pretty.dump(category(key)));
	}
}
