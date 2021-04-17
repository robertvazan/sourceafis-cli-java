// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.*;
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
		return Cache.get(TimingStats.class, Paths.get("benchmarks", "speed", "extractor"), Paths.get("measurement"), () -> {
			var templates = StreamEx.of(Fingerprint.all()).toMap(TemplateCache::load);
			var nondeterministic = new AtomicBoolean(false);
			var epoch = System.nanoTime();
			var strata = parallelize(() -> {
				var fingerprints = new ArrayList<>(Fingerprint.all());
				Collections.shuffle(fingerprints);
				var recorder = new TimingRecorder(epoch, DURATION, SAMPLE_SIZE);
				return () -> {
					while (true) {
						for (var fp : fingerprints) {
							var image = fp.decode();
							long start = System.nanoTime();
							var template = new FingerprintTemplate(image);
							long end = System.nanoTime();
							if (!Arrays.equals(templates.get(fp), template.toByteArray()))
								nondeterministic.set(true);;
							if (!recorder.record(fp.dataset, start, end))
								return recorder.complete();
						}
					}
				};
			});
			if (nondeterministic.get())
				logger.warn("Non-deterministic extractor.");
			return TimingStats.sum(SAMPLE_SIZE, strata);
		});
	}
}
