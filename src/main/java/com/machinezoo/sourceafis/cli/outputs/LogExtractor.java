// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class LogExtractor {
	public static Path identity(String key, Fingerprint fp) {
		return Cache.withExtension(fp.path(), Pretty.extension(ChecksumTransparencyExtractor.mime(fp, key)));
	}
	private static Path category(String key) {
		if (Configuration.normalized)
			return Paths.get("logs", "extractor", "normalized", key);
		else
			return Paths.get("logs", "extractor", key);
	}
	public static byte[] collect(String key, Fingerprint fp) {
		return Cache.get(byte[].class, category(key), identity(key, fp), () -> {
			var raw = Log.key(key, () -> new FingerprintTemplate(fp.decode())).get(0);
			return Configuration.normalized ? Serializer.normalize(ChecksumTransparencyExtractor.mime(fp, key), raw) : raw;
		});
	}
	public static void collect(String key) {
		for (var fp : Fingerprint.all())
			collect(key, fp);
		Pretty.print("Saved: " + Pretty.dump(category(key)));
	}
}
