// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.nio.file.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class ExtractorSpeed extends SpeedBenchmark {
	@Override
	protected TimingStats measure() {
		return Cache.get(TimingStats.class, Paths.get("benchmarks", "speed", "extractor"), Paths.get("sample"), () -> {
			var fingerprints = Fingerprint.all();
			var recorder = new TimingRecorder(System.nanoTime(), DURATION, SAMPLE_SIZE);
			while (true) {
				for (var fp : fingerprints) {
					var image = fp.decode();
					long start = System.nanoTime();
					new FingerprintTemplate(image);
					long end = System.nanoTime();
					if (!recorder.record(fp.dataset, start, end))
						return recorder.complete();
				}
			}
		});
	}
}
