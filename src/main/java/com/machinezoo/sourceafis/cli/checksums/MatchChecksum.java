// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;
import one.util.streamex.*;

public class MatchChecksum extends TransparencyChecksum<FingerprintPair> {
	@Override
	public String name() {
		return "match";
	}
	@Override
	public List<FingerprintPair> ids() {
		return StreamEx.of(Fingerprint.all()).flatMap(p -> StreamEx.of(p.dataset.fingerprints()).map(c -> new FingerprintPair(p, c))).toList();
	}
	@Override
	protected TransparencyTable checksum(FingerprintPair pair) {
		return Cache.get(TransparencyTable.class, category(), pair.path(), map -> {
			var dataset = pair.dataset;
			var fingerprints = dataset.fingerprints();
			var templates = StreamEx.of(fingerprints).map(fp -> TemplateCache.deserialize(fp)).toList();
			for (var probe : fingerprints) {
				var matcher = new FingerprintMatcher(templates.get(probe.id));
				for (var candidate : fingerprints) {
					var template = templates.get(candidate.id);
					var table = ChecksumCollector.collect(() -> matcher.match(template));
					map.put(new FingerprintPair(probe, candidate).path(), table);
				}
			}
		});
	}
}
