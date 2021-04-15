// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class ScoreCache {
	public static double[][] load(Dataset dataset) {
		return Cache.get(double[][].class, Paths.get("scores"), dataset.path(), () -> {
			var fingerprints = dataset.fingerprints();
			var templates = StreamEx.of(fingerprints).map(fp -> TemplateCache.deserialize(fp)).toList();
			var scores = new double[fingerprints.size()][];
			for (var probe : fingerprints) {
				var matcher = new FingerprintMatcher(templates.get(probe.id));
				scores[probe.id] = new double[fingerprints.size()];
				for (var candidate : fingerprints)
					scores[probe.id][candidate.id] = matcher.match(templates.get(candidate.id));
			}
			return scores;
		});
	}
}
