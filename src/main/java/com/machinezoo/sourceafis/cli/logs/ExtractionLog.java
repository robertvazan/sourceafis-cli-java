// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.logs;

import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.inputs.*;

public record ExtractionLog() implements LogOperation {
	@Override
	public String name() {
		return "extraction";
	}
	@Override
	public String description() {
		return "Log extractor transparency data for given key.";
	}
	@Override
	public void log(Dataset dataset, LogWriter writer) {
		var decoded = new DecodedImageCache(dataset).load();
		dataset.fingerprints().parallelStream()
			.forEach(fp -> writer.put(fp.path(), () -> new FingerprintTemplate(decoded.get(fp))));
	}
}
