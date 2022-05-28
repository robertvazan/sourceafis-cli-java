// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

import java.util.*;

public class VerificationSampler implements Sampler<FingerprintPair> {
	private final Random random = new Random();
	private final List<Fingerprint> fingerprints;
	public VerificationSampler(Profile profile) {
		fingerprints = profile.fingerprints();
	}
	public VerificationSampler() {
		this(Profile.everything());
	}
	@Override
	public FingerprintPair next() {
		var probe = fingerprints.get(random.nextInt(fingerprints.size()));
		var candidates = probe.finger().fingerprints().stream().filter(c -> !probe.equals(c)).toList();
		var candidate = candidates.get(random.nextInt(candidates.size()));
		return new FingerprintPair(probe, candidate);
	}
	@Override
	public Dataset dataset(FingerprintPair pair) {
		return pair.dataset();
	}
}
