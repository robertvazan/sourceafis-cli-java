// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public record FootprintCommand() implements SimpleCommand {
	@Override
	public List<String> subcommand() {
		return List.of("benchmark", "footprint");
	}
	@Override
	public String description() {
		return "Measure template footprint.";
	}
	public void print(List<Profile> profiles) {
		var table = new PrettyTable();
		for (var profile : profiles) {
			MissingBaselineException.silence().run(() -> {
				var stats = FootprintCache.sum(profile);
				table.add("Dataset", profile.name());
				table.add("Serialized", Pretty.bytes(stats.serialized(), profile.name(), "serialized"));
				table.add("Memory", Pretty.bytes(stats.memory(), profile.name(), "memory"));
				table.add("Minutiae", Pretty.minutiae(stats.minutiae(), profile.name(), "minutiae"));
			});
		}
		table.print();
	}
	@Override
	public void run() {
		print(Profile.all());
	}
}
