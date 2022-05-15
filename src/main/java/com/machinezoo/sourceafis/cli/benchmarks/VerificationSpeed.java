// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.cli.datasets.*;

public class VerificationSpeed extends MatchSpeed {
	@Override
	public String name() {
		return "verification";
	}
	@Override
	public String description() {
		return "Measure speed of verification, i.e. calling match() with matching candidate.";
	}
	@Override
	protected Sampler<FingerprintPair> sampler() {
		return new MatchingPairSampler();
	}
}
