// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.logs;

import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.outputs.*;

public record ProbeLog() implements LogOperation {
	@Override
	public String name() {
		return "probe";
	}
	@Override
	public String description() {
		return "Log transparency data for given key while preparing probe for matching.";
	}
	@Override
	public void log(Dataset dataset, LogWriter writer) {
		var templates = new TemplateCache(dataset).deserialize();
		dataset.fingerprints().parallelStream()
			.forEach(fp -> writer.put(fp.path(), () -> new FingerprintMatcher(templates.get(fp))));
	}
}
