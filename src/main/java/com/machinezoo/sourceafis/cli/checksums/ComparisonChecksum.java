// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.stream.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class ComparisonChecksum extends TransparencyChecksum<FingerprintPair> {
	@Override
	public String name() {
		return "comparison";
	}
	@Override
	public String description() {
		return "Compute consistency checksum of transparency data generated during comparison of probe to candidate.";
	}
	@Override
	public Stream<FingerprintPair> ids() {
		return Fingerprint.all().stream().flatMap(p -> p.dataset().fingerprints().stream().map(c -> new FingerprintPair(p, c)));
	}
	@Override
	protected TransparencyTable checksum(FingerprintPair pair) {
		return Cache.get(TransparencyTable.class, category(), pair.dataset().path(), pair.path(), batch -> {
			var dataset = pair.dataset();
			var fingerprints = dataset.fingerprints();
			var templates = fingerprints.parallelStream().map(fp -> TemplateCache.deserialize(fp)).toList();
			fingerprints.parallelStream().forEach(probe -> {
				var matcher = new FingerprintMatcher(templates.get(probe.id()));
				for (var candidate : fingerprints) {
					var template = templates.get(candidate.id());
					var table = ChecksumCollector.collect(() -> matcher.match(template));
					batch.add(new FingerprintPair(probe, candidate).path(), table);
				}
			});
		});
	}
}
