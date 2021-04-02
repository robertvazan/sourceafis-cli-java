// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class LogMatcher extends LogBase {
	private static Path identity(String key, FingerprintPair pair, int index, int count, String mime) {
		return identity(pair.path(), index, count, mime);
	}
	private static Path category(String key) {
		return category(key, "matcher");
	}
	private static byte[] collect(String key, FingerprintPair pair, int index, int count) {
		var mime = ChecksumTransparencyMatcher.mime(pair, key);
		return Cache.get(byte[].class, category(key), identity(key, pair, index, count, mime), map -> {
			var dataset = pair.dataset;
			var fingerprints = dataset.fingerprints();
			var templates = StreamEx.of(fingerprints).map(fp -> Template.of(fp)).toList();
			for (var probe : fingerprints) {
				var matcher = new FingerprintMatcher(templates.get(probe.id));
				for (var candidate : fingerprints) {
					var wpair = new FingerprintPair(probe, candidate);
					int wcount = ChecksumTransparencyMatcher.count(wpair, key);
					var template = templates.get(candidate.id);
					collect(key, index, wcount, mime, n -> identity(key, wpair, n, wcount, mime), () -> matcher.match(template), map);
				}
			}
		});
	}
	public static void collect(String key) {
		for (var probe : Fingerprint.all()) {
			for (var candidate : probe.dataset.fingerprints()) {
				var pair = new FingerprintPair(probe, candidate);
				int count = ChecksumTransparencyMatcher.count(pair, key);
				if (count > 0)
					collect(key, pair, 0, count);
			}
		}
		Pretty.print("Saved: " + Pretty.dump(category(key)));
	}
}
