// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.logs;

import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.checksums.*;
import com.machinezoo.sourceafis.cli.datasets.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;
import one.util.streamex.*;

public class MatchLog extends TransparencyLog<FingerprintPair> {
	@Override
	public String name() {
		return "match";
	}
	@Override
	public String description() {
		return "Log transparency data for given key during comparison of probe to candidate.";
	}
	@Override
	protected TransparencyChecksum<FingerprintPair> checksum() {
		return new MatchChecksum();
	}
	@Override
	protected byte[] log(String key, FingerprintPair pair, int index, int count, String mime) {
		return Cache.get(byte[].class, category(key), identity(pair, index, count, mime), batch -> {
			var dataset = pair.dataset;
			var fingerprints = dataset.fingerprints();
			var templates = StreamEx.of(fingerprints).map(fp -> TemplateCache.deserialize(fp)).toList();
			for (var probe : fingerprints) {
				var matcher = new FingerprintMatcher(templates.get(probe.id));
				for (var candidate : fingerprints) {
					var wpair = new FingerprintPair(probe, candidate);
					int wcount = new MatchChecksum().count(wpair, key);
					var template = templates.get(candidate.id);
					log(key, wpair, index, wcount, mime, () -> matcher.match(template), batch);
				}
			}
		});
	}
}
