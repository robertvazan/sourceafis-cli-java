// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.outputs.*;

public record ProbeChecksum() implements ChecksumOperation {
	@Override
	public String name() {
		return "probe";
	}
	@Override
	public String description() {
		return "Compute consistency checksum of transparency data generated when preparing probe for matching.";
	}
	@Override
	public ChecksumTable checksum(Dataset dataset) {
		var templates = new TemplateCache(dataset).deserialize();
		return ChecksumTable.sum(dataset.fingerprints().parallelStream()
			.map(fp -> ChecksumCollector.collect(() -> new FingerprintMatcher(templates.get(fp))))
			.toList());
	}
}
