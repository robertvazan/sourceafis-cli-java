// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.samples.*;
import one.util.streamex.*;

public class ProbeSpeed extends SoloSpeed {
	@Override
	public String name() {
		return "probe";
	}
	@Override
	protected TimingStats measure() {
		var templates = StreamEx.of(Fingerprint.all()).toMap(TemplateCache::deserialize);
		var scores = StreamEx.of(Dataset.all()).toMap(ds -> ScoreCache.load(ds));
		return measure(() -> new TimedOperation<Fingerprint>() {
			FingerprintTemplate template;
			FingerprintMatcher matcher;
			double expected;
			@Override
			public void prepare(Fingerprint fp) {
				template = templates.get(fp);
				expected = scores.get(fp.dataset)[fp.id][fp.id];
			}
			@Override
			public void execute() {
				matcher = new FingerprintMatcher(template);
			}
			@Override
			public boolean verify() {
				return matcher.match(template) == expected;
			}
		});
	}
}
