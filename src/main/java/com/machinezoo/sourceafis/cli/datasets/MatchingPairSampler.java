// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

import java.util.*;

public class MatchingPairSampler extends PairSampler {
	public MatchingPairSampler(Profile profile) {
		super(profile);
	}
	public MatchingPairSampler() {
		this(Profile.everything());
	}
	@Override
	protected List<Fingerprint> candidates(Fingerprint probe) {
		return probe.finger().fingerprints().stream().filter(c -> !probe.equals(c)).toList();
	}
}
