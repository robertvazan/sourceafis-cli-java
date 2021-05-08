// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.args.*;

public class SpeedOverview extends Command {
	@Override
	public List<String> subcommand() {
		return List.of("benchmark", "speed");
	}
	@Override
	public String description() {
		return "Measure algorithm speed.";
	}
	@Override
	public void run() {
		var benchmarks = List.of(
			new ExtractionSpeed(),
			new IdentificationSpeed(),
			new VerificationSpeed(),
			new ProbeSpeed(),
			new SerializationSpeed(),
			new DeserializationSpeed());
		var table = new SpeedTable("Operation");
		for (var benchmark : benchmarks)
			table.add(benchmark.name(), benchmark.measure().skip(SpeedBenchmark.WARMUP));
		table.print();
	}
}
