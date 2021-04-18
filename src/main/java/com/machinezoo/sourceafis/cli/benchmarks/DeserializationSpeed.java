// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
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
	protected TimingStats measure() {
		var serialized = StreamEx.of(Fingerprint.all()).toMap(TemplateCache::load);
		return measure(() -> new TimedOperation<Fingerprint>() {
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
		});
	}
}
