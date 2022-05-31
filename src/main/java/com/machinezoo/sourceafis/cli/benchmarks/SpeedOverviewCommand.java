// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class SpeedOverviewCommand implements SimpleCommand {
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
			new ExtractionSpeedCache(),
			new IdentificationSpeedCache(),
			new VerificationSpeedCache(),
			new DeserializationSpeedCache());
		var table = new SpeedTable("Operation");
		for (var benchmark : benchmarks)
			MissingBaselineException.silence().run(() -> table.add(benchmark.name(), benchmark.get().skip(SpeedCache.WARMUP)));
		table.print();
	}
}
