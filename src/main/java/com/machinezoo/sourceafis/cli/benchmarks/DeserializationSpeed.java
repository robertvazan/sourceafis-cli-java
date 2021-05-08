// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.samples.*;
import one.util.streamex.*;

public class DeserializationSpeed extends SoloSpeed {
	@Override
	public String name() {
		return "deserialization";
	}
	@Override
	public String description() {
		return "Measure speed of template deserialization.";
	}
	@Override
	public TimingStats measure() {
		return measure(() -> {
			var serialized = StreamEx.of(Fingerprint.all()).toMap(TemplateCache::load);
			return () -> new TimedOperation<Fingerprint>() {
				byte[] input;
				FingerprintTemplate deserialized;
				@Override
				public void prepare(Fingerprint fp) {
					input = serialized.get(fp);
				}
				@Override
				public void execute() {
					deserialized = new FingerprintTemplate(input);
				}
				@Override
				public boolean verify() {
					return Arrays.equals(input, deserialized.toByteArray());
				}
			};
		});
	}
}
