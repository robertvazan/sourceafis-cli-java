// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.stream.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.datasets.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class ExtractorChecksum extends TransparencyChecksum<Fingerprint> {
	@Override
	public String name() {
		return "extractor";
	}
	@Override
	public String description() {
		return "Compute consistency checksum of extractor transparency data.";
	}
	@Override
	public Stream<Fingerprint> ids() {
		return Fingerprint.all().parallelStream();
	}
	@Override
	protected TransparencyTable checksum(Fingerprint fp) {
		return Cache.get(TransparencyTable.class, category(), fp.path(), () -> {
			return ChecksumCollector.collect(() -> new FingerprintTemplate(fp.decode()));
		});
	}
}
