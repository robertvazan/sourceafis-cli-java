// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.datasets.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class ScoreCache {
	public static double[][] load(Dataset dataset) {
		return Cache.get(double[][].class, Paths.get("scores"), dataset.path(), () -> {
			var fingerprints = dataset.fingerprints();
			var templates = fingerprints.parallelStream().map(fp -> TemplateCache.deserialize(fp)).toList();
			var scores = new double[fingerprints.size()][];
			fingerprints.parallelStream().forEach(probe -> {
				var matcher = new FingerprintMatcher(templates.get(probe.id()));
				scores[probe.id()] = new double[fingerprints.size()];
				for (var candidate : fingerprints)
					scores[probe.id()][candidate.id()] = matcher.match(templates.get(candidate.id()));
			});
			return scores;
		});
	}
}
