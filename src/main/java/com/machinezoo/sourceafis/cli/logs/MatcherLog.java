// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.logs;

import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.checksums.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class MatcherLog extends TransparencyLog<Fingerprint> {
	@Override
	public String name() {
		return "matcher";
	}
	@Override
	protected TransparencyChecksum<Fingerprint> checksum() {
		return new MatcherChecksum();
	}
	@Override
	protected byte[] log(String key, Fingerprint fp, int index, int count, String mime) {
		return Cache.get(byte[].class, category(key), identity(key, fp, index, count, mime), map -> {
			var template = TemplateCache.deserialize(fp);
			log(key, fp, index, count, mime, () -> new FingerprintMatcher(template), map);
		});
	}
}