// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.nio.file.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public record TemplateChecksumCache(Dataset dataset) implements PerDatasetCache<TemplateStats> {
	@Override
	public Path category() {
		return Paths.get("checksums", "templates");
	}
	@Override
	public Class<TemplateStats> type() {
		return TemplateStats.class;
	}
	@Override
	public TemplateStats compute() {
		var templates = new TemplateCache(dataset).load();
		return TemplateStats.sum(dataset.fingerprints().parallelStream()
			.map(fp -> {
				var serialized = templates.get(fp);
				var normalized = Serializer.normalize(serialized);
				return new TemplateStats(
					1,
					serialized.length,
					normalized.length,
					Hasher.hash(normalized));
			})
			.toList());
	}
	public static TemplateStats sum(Profile profile) {
		return TemplateStats.sum(profile.datasets().stream().map(ds -> new TemplateChecksumCache(ds).get()).toList());
	}
	public static byte[] global() {
		return sum(Profile.everything()).hash();
	}
}
