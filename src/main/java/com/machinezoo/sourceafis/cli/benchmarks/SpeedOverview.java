// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;

public class SpeedOverview implements Runnable {
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
