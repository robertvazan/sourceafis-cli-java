// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.sourceafis.cli.datasets.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class TemplateChecksum extends Command {
	@Override
	public List<String> subcommand() {
		return List.of("checksum", "templates");
	}
	@Override
	public String description() {
		return "Compute consistency checksum of templates.";
	}
	private TemplateStats checksum(Fingerprint fp) {
		return Cache.get(TemplateStats.class, Paths.get("checksums", "templates"), fp.path(), () -> {
			var checksum = new TemplateStats();
			var serialized = TemplateCache.load(fp);
			checksum.count = 1;
			checksum.length = serialized.length;
			var normalized = Serializer.normalize(serialized);
			checksum.normalized = normalized.length;
			checksum.hash = Hasher.hash(normalized);
			return checksum;
		});
	}
	private TemplateStats checksum(Profile profile) {
		return TemplateStats.sum(profile.fingerprints().parallelStream().map(this::checksum).toList());
	}
	public byte[] global() {
		return checksum(Profile.everything()).hash;
	}
	@Override
	public void run() {
		var table = new PrettyTable("Dataset", "Count", "Length", "Normalized", "Total", "Hash");
		for (var profile : Profile.all()) {
			var stats = checksum(profile);
			table.add(
				profile.name(),
				Pretty.length(stats.count),
				Pretty.length(stats.length / stats.count, profile.name(), "length"),
				Pretty.length(stats.normalized / stats.count, profile.name(), "normalized"),
				Pretty.length(stats.normalized, profile.name(), "total"),
				Pretty.hash(stats.hash, profile.name(), "hash"));
		}
		Pretty.print(table.format());
	}
}
