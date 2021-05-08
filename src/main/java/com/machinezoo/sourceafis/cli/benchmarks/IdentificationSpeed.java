// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.cli.samples.*;

public class IdentificationSpeed extends MatchSpeed {
	@Override
	public String name() {
		return "identification";
	}
	@Override
	public String description() {
		return "Measure speed of identification, i.e. calling match() with non-matching candidate.";
	}
	@Override
	protected boolean filter(FingerprintPair pair) {
		return !pair.probe().finger().equals(pair.candidate().finger());
	}
}
