// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.samples.*;
import one.util.streamex.*;

public class SerializationSpeed extends SoloSpeed {
	@Override
	public String name() {
		return "serialization";
	}
	@Override
	public TimingStats measure() {
		var templates = StreamEx.of(Fingerprint.all()).toMap(TemplateCache::deserialize);
		var serialized = StreamEx.of(Fingerprint.all()).toMap(TemplateCache::load);
		return measure(() -> new TimedOperation<Fingerprint>() {
			FingerprintTemplate template;
			byte[] output;
			byte[] expected;
			@Override
			public void prepare(Fingerprint fp) {
				template = templates.get(fp);
				expected = serialized.get(fp);
			}
			@Override
			public void execute() {
				output = template.toByteArray();
			}
			@Override
			public boolean verify() {
				return Arrays.equals(expected, output);
			}
		});
	}
}