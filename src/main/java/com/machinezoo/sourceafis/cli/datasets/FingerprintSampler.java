// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

import java.util.*;

public class FingerprintSampler implements Sampler<Fingerprint> {
	private final Random random = new Random();
	private final List<Fingerprint> fingerprints;
	public FingerprintSampler(Profile profile) {
		fingerprints = profile.fingerprints();
	}
	public FingerprintSampler() {
		this(Profile.everything());
	}
	@Override
	public Fingerprint next() {
		return fingerprints.get(random.nextInt(fingerprints.size()));
	}
	@Override
	public Dataset dataset(Fingerprint fp) {
		return fp.dataset;
	}
}
