// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

import java.time.*;
import java.util.*;

public class IdentificationSampler implements Sampler<CrossDatasetPair> {
	/*
	 * 100ms is enough to drown out probe overhead.
	 */
	private static final Duration BATCH_INTERVAL = Duration.ofMillis(100);
	/*
	 * Current algorithm costs about 80us per candidate on dev hardware. This does not need to be precise.
	 */
	private static final int BATCH = (int)(BATCH_INTERVAL.toNanos() / 80_000);
	private final Random random = new Random();
	private final List<Fingerprint> fingerprints;
	private Fingerprint probe;
	private int remaining;
	public IdentificationSampler(Profile profile) {
		fingerprints = profile.fingerprints();
	}
	public IdentificationSampler() {
		this(Profile.everything());
	}
	@Override
	public CrossDatasetPair next() {
		if (remaining <= 0) {
			probe = fingerprints.get(random.nextInt(fingerprints.size()));
			remaining = BATCH;
		}
		--remaining;
		while (true) {
			var candidate = fingerprints.get(random.nextInt(fingerprints.size()));
			if (!probe.finger().equals(candidate.finger()))
				return new CrossDatasetPair(probe, candidate);
		}
	}
	@Override
	public Dataset dataset(CrossDatasetPair pair) {
		return random.nextBoolean() ? pair.probe().dataset : pair.candidate().dataset;
	}
}
