// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.stream.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class ProbeChecksum extends TransparencyChecksum<Fingerprint> {
	@Override
	public String name() {
		return "probe";
	}
	@Override
	public String description() {
		return "Compute consistency checksum of transparency data generated when preparing probe for matching.";
	}
	@Override
	public Stream<Fingerprint> ids() {
		return Fingerprint.all().parallelStream();
	}
	@Override
	protected TransparencyTable checksum(Fingerprint fp) {
		return Cache.get(TransparencyTable.class, category(), fp.path(), () -> {
			var template = TemplateCache.deserialize(fp);
			return ChecksumCollector.collect(() -> new FingerprintMatcher(template));
		});
	}
}
