// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.cli.samples.*;

public class VerificationSpeed extends MatchSpeed {
	@Override
	public String name() {
		return "verification";
	}
	@Override
	protected boolean filter(FingerprintPair pair) {
		return pair.probe().finger().equals(pair.candidate().finger()) && !pair.probe().equals(pair.candidate());
	}
}
