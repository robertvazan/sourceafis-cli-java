// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.datasets.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import one.util.streamex.*;

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
			var templates = StreamEx.of(Fingerprint.all()).toMap(TemplateCache::load);
			return () -> new TimedOperation<Fingerprint>() {
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
			};
		});
	}
}
