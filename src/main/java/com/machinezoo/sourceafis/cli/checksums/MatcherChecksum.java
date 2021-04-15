// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class MatcherChecksum extends TransparencyChecksum<Fingerprint> {
	@Override
	public String name() {
		return "matcher";
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
