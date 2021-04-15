// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.checksums;

import java.nio.file.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class TemplateChecksum implements Runnable {
	private TemplateStats checksum(Fingerprint fp) {
		return Cache.get(TemplateStats.class, Paths.get("checksums", "templates"), fp.path(), () -> {
			var checksum = new TemplateStats();
			var serialized = TemplateCache.load(fp);
			checksum.count = 1;
			checksum.length = serialized.length;
			var normalized = Serializer.normalize(serialized);
			checksum.normalized = normalized.length;
			checksum.hash = Hash.of(normalized);
			return checksum;
		});
	}
	private TemplateStats checksum(Profile profile) {
		return TemplateStats.sum(StreamEx.of(profile.fingerprints()).map(this::checksum).toList());
	}
	public byte[] global() {
		return checksum(Profile.everything()).hash;
	}
	@Override
	public void run() {
		var table = new Pretty.Table("Dataset", "Count", "Length", "Normalized", "Total", "Hash");
		for (var profile : Profile.all()) {
			var stats = checksum(profile);
			table.add(
				profile.name,
				Pretty.length(stats.count),
				Pretty.length(stats.length / stats.count, profile.name, "length"),
				Pretty.length(stats.normalized / stats.count, profile.name, "normalized"),
				Pretty.length(stats.normalized, profile.name, "total"),
				Pretty.hash(stats.hash, profile.name, "hash"));
		}
		Pretty.print(table.format());
	}
}
