// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.transparency;

import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.checksums.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class MatchLog extends TransparencyLog<FingerprintPair> {
	@Override
	public String name() {
		return "match";
	}
	@Override
	protected TransparencyChecksum<FingerprintPair> checksum() {
		return new MatchChecksum();
	}
	@Override
	protected byte[] log(String key, FingerprintPair pair, int index, int count, String mime) {
		return Cache.get(byte[].class, category(key), identity(key, pair, index, count, mime), map -> {
			var dataset = pair.dataset;
			var fingerprints = dataset.fingerprints();
			var templates = StreamEx.of(fingerprints).map(fp -> TemplateCache.deserialize(fp)).toList();
			for (var probe : fingerprints) {
				var matcher = new FingerprintMatcher(templates.get(probe.id));
				for (var candidate : fingerprints) {
					var wpair = new FingerprintPair(probe, candidate);
					int wcount = new MatchChecksum().count(wpair, key);
					var template = templates.get(candidate.id);
					log(key, wpair, index, wcount, mime, () -> matcher.match(template), map);
				}
			}
		});
	}
}
