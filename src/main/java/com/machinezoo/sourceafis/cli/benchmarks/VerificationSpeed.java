// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
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
