// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.inputs.*;

public record ExtractionChecksum() implements ChecksumOperation {
	@Override
	public String name() {
		return "extraction";
	}
	@Override
	public String description() {
		return "Compute consistency checksum of extractor transparency data.";
	}
	@Override
	public ChecksumTable checksum(Dataset dataset) {
		var decoded = new DecodedImageCache(dataset).load();
		return ChecksumTable.sum(dataset.fingerprints().parallelStream()
			.map(fp -> ChecksumCollector.collect(() -> new FingerprintTemplate(decoded.get(fp))))
			.toList());
	}
}
