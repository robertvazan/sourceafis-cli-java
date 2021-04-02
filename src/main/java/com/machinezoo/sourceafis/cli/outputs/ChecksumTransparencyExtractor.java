// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class ChecksumTransparencyExtractor extends ChecksumTransparencyBase {
	private static Table checksum(Fingerprint fp) {
		return Cache.get(Table.class, Paths.get("checksums", "transparency", "extractor"), fp.path(), () -> {
			return collect(() -> new FingerprintTemplate(fp.decode()));
		});
	}
	public static int count(Fingerprint fp, String key) {
		return count(checksum(fp), key);
	}
	public static String mime(Fingerprint fp, String key) {
		return mime(checksum(fp), key);
	}
	private static Table checksum() {
		return merge(StreamEx.of(Fingerprint.all()).map(fp -> checksum(fp)).toList());
	}
	public static void report() {
		report(checksum());
	}
}
