// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.logs;

import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.checksums.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class ComparisonLog extends TransparencyLog<FingerprintPair> {
	@Override
	public String name() {
		return "comparison";
	}
	@Override
	public String description() {
		return "Log transparency data for given key during comparison of probe to candidate.";
	}
	@Override
	protected TransparencyChecksum<FingerprintPair> checksum() {
		return new ComparisonChecksum();
	}
	@Override
	protected byte[] log(String key, FingerprintPair pair, int index, int count, String mime) {
		return Cache.get(byte[].class, category(key), pair.dataset().path(), identity(pair, index, count, mime), batch -> {
			var dataset = pair.dataset();
			var fingerprints = dataset.fingerprints();
			var templates = fingerprints.parallelStream().map(fp -> TemplateCache.deserialize(fp)).toList();
			fingerprints.parallelStream().forEach(probe -> {
				var matcher = new FingerprintMatcher(templates.get(probe.id()));
				for (var candidate : fingerprints) {
					var wpair = new FingerprintPair(probe, candidate);
					int wcount = new ComparisonChecksum().count(wpair, key);
					var template = templates.get(candidate.id());
					log(key, wpair, wcount, mime, () -> matcher.match(template), batch);
				}
			});
		});
	}
}
