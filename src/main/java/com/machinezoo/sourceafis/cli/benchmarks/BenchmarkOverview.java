// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class BenchmarkOverview implements Runnable {
	@Override
	public void run() {
		new AccuracyBenchmark().print(Profile.aggegate());
		Pretty.print("");
		new FootprintBenchmark().print(Profile.aggegate());
		Pretty.print("");
		new SpeedOverview().run();
	}
}
