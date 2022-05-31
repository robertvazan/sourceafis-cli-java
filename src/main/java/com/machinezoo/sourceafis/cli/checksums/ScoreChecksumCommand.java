// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class ScoreChecksumCommand implements SimpleCommand {
	@Override
	public List<String> subcommand() {
		return List.of("checksum", "scores");
	}
	@Override
	public String description() {
		return "Compute consistency checksum of similarity scores.";
	}
	@Override
	public void run() {
		var table = new PrettyTable();
		for (var profile : Profile.all()) {
			MissingBaselineException.silence().run(() -> {
				var stats = ScoreChecksumCache.sum(profile);
				table.add("Dataset", profile.name());
				table.add("Matching", Pretty.decibans(stats.matching(), profile.name(), "matching"));
				table.add("Non-matching", Pretty.decibans(stats.nonmatching(), profile.name(), "nonmatching"));
				table.add("Self-matching", Pretty.decibans(stats.selfmatching(), profile.name(), "selfmatching"));
				table.add("Hash", Pretty.hash(stats.hash(), profile.name(), "hash"));
			});
		}
		table.print();
	}
}
