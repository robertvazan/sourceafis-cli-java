// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.cli.samples.*;

public class IdentificationSpeed extends MatchSpeed {
	@Override
	public String name() {
		return "identification";
	}
	@Override
	protected boolean filter(FingerprintPair pair) {
		return !pair.probe().finger().equals(pair.candidate().finger());
	}
}
