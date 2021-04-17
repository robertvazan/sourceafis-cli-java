// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.nio.file.*;
import java.util.*;
import org.slf4j.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;
import one.util.streamex.*;

public class ExtractorSpeed extends SpeedBenchmark {
	private static final Logger logger = LoggerFactory.getLogger(ExtractorSpeed.class);
	@Override
	protected TimingStats measure() {
		return Cache.get(TimingStats.class, Paths.get("benchmarks", "speed", "extractor"), Paths.get("sample"), () -> {
			var fingerprints = new ArrayList<>(Fingerprint.all());
			Collections.shuffle(fingerprints);
			var templates = StreamEx.of(fingerprints).toMap(TemplateCache::load);
			var recorder = new TimingRecorder(System.nanoTime(), DURATION, SAMPLE_SIZE);
			boolean nondeterministic = false;
			while (true) {
				for (var fp : fingerprints) {
					var image = fp.decode();
					long start = System.nanoTime();
					var template = new FingerprintTemplate(image);
					long end = System.nanoTime();
					if (!Arrays.equals(templates.get(fp), template.toByteArray()))
						nondeterministic = true;
					if (!recorder.record(fp.dataset, start, end)) {
						if (nondeterministic)
							logger.warn("Non-deterministic extractor.");
						return recorder.complete();
					}
				}
			}
		});
	}
}
