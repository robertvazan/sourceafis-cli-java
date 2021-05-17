// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.datasets.*;
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
	public List<Fingerprint> ids() {
		return Fingerprint.all();
	}
	@Override
	protected TransparencyTable checksum(Fingerprint fp) {
		return Cache.get(TransparencyTable.class, category(), fp.path(), () -> {
			var template = TemplateCache.deserialize(fp);
			return ChecksumCollector.collect(() -> new FingerprintMatcher(template));
		});
	}
}
