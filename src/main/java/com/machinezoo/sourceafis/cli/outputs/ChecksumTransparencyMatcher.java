// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class ChecksumTransparencyMatcher extends ChecksumTransparencyBase {
	private static Table checksum(FingerprintPair pair) {
		return Cache.get(Table.class, Paths.get("checksums", "transparency", "matcher"), pair.path(), map -> {
			var dataset = pair.dataset;
			var fingerprints = dataset.fingerprints();
			var templates = StreamEx.of(fingerprints).map(fp -> Template.of(fp)).toList();
			for (var probe : fingerprints) {
				var matcher = new FingerprintMatcher(templates.get(probe.id));
				for (var candidate : fingerprints) {
					var template = templates.get(candidate.id);
					var table = collect(() -> matcher.match(template));
					map.put(new FingerprintPair(probe, candidate).path(), table);
				}
			}
		});
	}
	private static Table checksum(Fingerprint probe) {
		return Cache.get(Table.class, Paths.get("checksums", "transparency", "matcher"), probe.path(), () -> {
			var tables = new ArrayList<Table>();
			for (var candidate : probe.dataset.fingerprints())
				tables.add(checksum(new FingerprintPair(probe, candidate)));
			return merge(tables);
		});
	}
	public static int count(FingerprintPair pair, String key) {
		return count(checksum(pair), key);
	}
	public static String mime(FingerprintPair pair, String key) {
		return mime(checksum(pair), key);
	}
	private static Table checksum() {
		return merge(StreamEx.of(Fingerprint.all()).map(fp -> checksum(fp)).toList());
	}
	public static void report() {
		report(checksum());
	}
}
