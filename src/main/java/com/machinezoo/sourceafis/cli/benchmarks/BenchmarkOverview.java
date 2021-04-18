// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
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
