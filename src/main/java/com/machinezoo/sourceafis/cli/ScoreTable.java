// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.nio.file.*;
import com.machinezoo.sourceafis.*;
import one.util.streamex.*;

class ScoreTable {
	static double[][] of(SampleDataset dataset) {
		return PersistentCache.get(double[][].class, Paths.get("scores"), dataset.path(), () -> {
			var fingerprints = dataset.fingerprints();
			var templates = StreamEx.of(fingerprints).map(fp -> NativeTemplate.of(fp)).toList();
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
