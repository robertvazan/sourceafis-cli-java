// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.samples.*;

public abstract class SoloSpeed extends SpeedBenchmark<Fingerprint> {
	@Override
	protected Dataset dataset(Fingerprint fp) {
		return fp.dataset;
	}
	@Override
	protected List<Fingerprint> shuffle() {
		return shuffle(Fingerprint.all());
	}
}
