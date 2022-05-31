// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.logs;

import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.outputs.*;

public record ComparisonLog() implements LogOperation {
	@Override
	public String name() {
		return "comparison";
	}
	@Override
	public String description() {
		return "Log transparency data for given key during comparison of probe to candidate.";
	}
	@Override
	public void log(Dataset dataset, LogWriter writer) {
		var templates = new TemplateCache(dataset).deserialize();
		var fingerprints = dataset.fingerprints();
		fingerprints.parallelStream()
			.forEach(probe -> {
				var matcher = new FingerprintMatcher(templates.get(probe));
				for (var candidate : fingerprints)
					writer.put(new FingerprintPair(probe, candidate).path(), () -> matcher.match(templates.get(candidate)));
			});
	}
}
