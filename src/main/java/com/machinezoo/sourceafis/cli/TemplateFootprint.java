// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.nio.file.*;
import java.util.*;
import org.slf4j.*;
import one.util.streamex.*;

class TemplateFootprint {
	int count;
	int serialized;
	byte[] hash;
	static TemplateFootprint of(SampleFingerprint fp) {
		return PersistentCache.get(TemplateFootprint.class, Paths.get("footprints"), fp.path(), () -> {
			var footprint = new TemplateFootprint();
			var serialized = NativeTemplate.serialized(fp);
			footprint.count = 1;
			footprint.serialized = serialized.length;
			footprint.hash = DataHash.of(serialized);
			return footprint;
		});
	}
	static TemplateFootprint sum(List<TemplateFootprint> list) {
		var sum = new TemplateFootprint();
		var hash = new DataHash();
		for (var footprint : list) {
			sum.count += footprint.count;
			sum.serialized += footprint.serialized;
			hash.add(footprint.hash);
		}
		sum.hash = hash.compute();
		return sum;
	}
	static TemplateFootprint sum() {
		return sum(StreamEx.of(SampleFingerprint.all()).map(fp -> of(fp)).toList());
	}
	private static final Logger logger = LoggerFactory.getLogger(TemplateFootprint.class);
	static void report() {
		var sum = sum();
		logger.info("Template footprint: {} B serialized", sum.serialized / sum.count);
		logger.info("Template hash: {}", DataHash.format(sum.hash));
	}
}
