// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.samples.*;
import one.util.streamex.*;

public class ExtractorSpeed extends SpeedBenchmark<Fingerprint> {
	@Override
	public String name() {
		return "extractor";
	}
	@Override
	protected Dataset dataset(Fingerprint fp) {
		return fp.dataset;
	}
	@Override
	protected List<Fingerprint> shuffle() {
		var fingerprints = new ArrayList<>(Fingerprint.all());
		Collections.shuffle(fingerprints);
		return fingerprints;
	}
	@Override
	protected TimingStats measure() {
		var templates = StreamEx.of(Fingerprint.all()).toMap(TemplateCache::load);
		return measure(new TimedOperation<Fingerprint>() {
			FingerprintImage image;
			FingerprintTemplate template;
			byte[] expected;
			@Override
			public void prepare(Fingerprint fp) {
				image = fp.decode();
				expected = templates.get(fp);
			}
			@Override
			public void execute() {
				template = new FingerprintTemplate(image);
			}
			@Override
			public boolean verify() {
				return Arrays.equals(expected, template.toByteArray());
			}
		});
	}
}
