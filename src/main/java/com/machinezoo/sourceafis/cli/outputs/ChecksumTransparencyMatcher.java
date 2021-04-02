// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class ChecksumTransparencyMatcher {
	public static ChecksumTransparency.Table checksum(Fingerprint probe) {
		return Cache.get(ChecksumTransparency.Table.class, Paths.get("checksums", "transparency", "matcher"), probe.path(), () -> {
			var matcher = new FingerprintMatcher(Template.of(probe));
			var tables = new ArrayList<ChecksumTransparency.Table>();
			for (var candidate : probe.dataset.fingerprints()) {
				var template = Template.of(candidate);
				tables.add(ChecksumTransparency.collect(() -> matcher.match(template)));
			}
			return ChecksumTransparency.Table.merge(tables);
		});
	}
	public static ChecksumTransparency.Table checksum() {
		return ChecksumTransparency.Table.merge(StreamEx.of(Fingerprint.all()).map(fp -> checksum(fp)).toList());
	}
	public static void report() {
		ChecksumTransparency.report(checksum());
	}
}
