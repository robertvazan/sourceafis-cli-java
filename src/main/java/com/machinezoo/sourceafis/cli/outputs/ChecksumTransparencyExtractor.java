// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class ChecksumTransparencyExtractor {
	public static ChecksumTransparency.Table checksum(Fingerprint fp) {
		return Cache.get(ChecksumTransparency.Table.class, Paths.get("checksums", "transparency", "extractor"), fp.path(), () -> {
			return ChecksumTransparency.collect(() -> new FingerprintTemplate(fp.decode()));
		});
	}
	public static String mime(Fingerprint fp, String key) {
		return checksum(fp).rows.stream()
			.filter(r -> r.key.equals(key))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Transparency key not found: " + key))
			.stats.mime;
	}
	public static ChecksumTransparency.Table checksum() {
		return ChecksumTransparency.Table.merge(StreamEx.of(Fingerprint.all()).map(fp -> checksum(fp)).toList());
	}
	public static void report() {
		ChecksumTransparency.report(checksum());
	}
}
