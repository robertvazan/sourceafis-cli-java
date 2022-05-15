// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.datasets.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class SerializationSpeed extends SpeedBenchmark<Fingerprint> {
	@Override
	public String name() {
		return "serialization";
	}
	@Override
	public String description() {
		return "Measure speed of template serialization.";
	}
	@Override
	protected Sampler<Fingerprint> sampler() {
		return new FingerprintSampler();
	}
	@Override
	public TimingStats measure() {
		return measure(() -> {
			var templates = StreamEx.of(Fingerprint.all()).parallel().toMap(TemplateCache::deserialize);
			return () -> new TimedOperation<Fingerprint>() {
				FingerprintTemplate template;
				byte[] output;
				@Override
				public void prepare(Fingerprint fp) {
					template = templates.get(fp);
				}
				@Override
				public void execute() {
					output = template.toByteArray();
				}
				@Override
				public void blackhole(Hasher hasher) {
					hasher.add(output);
				}
			};
		});
	}
}
