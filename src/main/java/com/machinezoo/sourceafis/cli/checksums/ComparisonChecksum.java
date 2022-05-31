// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.outputs.*;

public record ComparisonChecksum() implements ChecksumOperation {
	@Override
	public String name() {
		return "comparison";
	}
	@Override
	public String description() {
		return "Compute consistency checksum of transparency data generated during comparison of probe to candidate.";
	}
	@Override
	public ChecksumTable checksum(Dataset dataset) {
		var templates = new TemplateCache(dataset).deserialize();
		var fingerprints = dataset.fingerprints();
		return ChecksumTable.sum(fingerprints.parallelStream()
			.map(probe -> {
				var matcher = new FingerprintMatcher(templates.get(probe));
				return ChecksumTable.sum(fingerprints.stream()
					.map(candidate -> ChecksumCollector.collect(() -> matcher.match(templates.get(candidate))))
					.toList());
			})
			.toList());
	}
}
