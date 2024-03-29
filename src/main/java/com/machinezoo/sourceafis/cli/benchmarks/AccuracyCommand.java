// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;
import one.util.streamex.*;

public record AccuracyCommand() implements SimpleCommand {
	@Override
	public List<String> subcommand() {
		return List.of("benchmark", "accuracy");
	}
	@Override
	public String description() {
		return "Measure algorithm accuracy.";
	}
	private AccuracyStats sum(Profile profile) {
		return AccuracyStats.sum(StreamEx.of(profile.datasets()).map(ds -> new AccuracyCache(ds).get()).toList());
	}
	public void print(List<Profile> profiles) {
		var table = new PrettyTable();
		for (var profile : profiles) {
			MissingBaselineException.silence().run(() -> {
				var stats = sum(profile);
				table.add("Dataset", profile.name());
				table.add("EER", Pretty.accuracy(stats.eer(), profile.name(), "EER"));
				table.add("FMR100", Pretty.accuracy(stats.fmr100(), profile.name(), "FMR100"));
				table.add("FMR1K", Pretty.accuracy(stats.fmr1K(), profile.name(), "FMR1K"));
				table.add("FMR10K", Pretty.accuracy(stats.fmr10K(), profile.name(), "FMR10K"));
			});
		}
		table.print();
	}
	@Override
	public void run() {
		print(Profile.all());
	}
}
