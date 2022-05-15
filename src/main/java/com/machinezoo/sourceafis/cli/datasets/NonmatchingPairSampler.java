// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

import java.util.*;

public class NonmatchingPairSampler extends PairSampler {
	public NonmatchingPairSampler(Profile profile) {
		super(profile);
	}
	public NonmatchingPairSampler() {
		this(Profile.everything());
	}
	@Override
	protected List<Fingerprint> candidates(Fingerprint probe) {
		return probe.dataset.fingerprints().stream().filter(c -> !probe.finger().equals(c.finger())).toList();
	}
}
