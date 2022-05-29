// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;

public class BenchmarkOverview extends Command {
	@Override
	public List<String> subcommand() {
		return List.of("benchmark");
	}
	@Override
	public String description() {
		return "Measure algorithm accuracy, template footprint, and implementation speed.";
	}
	@Override
	public void run() {
		new AccuracyBenchmark().print(Profile.aggregate());
		Pretty.print("");
		new FootprintBenchmark().print(Profile.aggregate());
		Pretty.print("");
		new SpeedOverview().run();
	}
}
