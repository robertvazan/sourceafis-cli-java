// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.datasets.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class ExtractionSpeed extends SoloSpeed {
	@Override
	public String name() {
		return "extraction";
	}
	@Override
	public String description() {
		return "Measure speed of feature extraction, i.e. FingerprintTemplate constructor.";
	}
	@Override
	public TimingStats measure() {
		return measure(() -> {
			return () -> new TimedOperation<Fingerprint>() {
				FingerprintImage image;
				FingerprintTemplate template;
				@Override
				public void prepare(Fingerprint fp) {
					image = fp.decode();
				}
				@Override
				public void execute() {
					template = new FingerprintTemplate(image);
				}
				@Override
				public void blackhole(Hasher hasher) {
					hasher.add(template.toByteArray());
				}
			};
		});
	}
}
