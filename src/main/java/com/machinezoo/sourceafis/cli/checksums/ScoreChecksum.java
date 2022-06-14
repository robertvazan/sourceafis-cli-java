// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.sourceafis.cli.benchmarks.*;
import com.machinezoo.sourceafis.cli.datasets.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class ScoreChecksum extends Command {
	@Override
	public List<String> subcommand() {
		return List.of("checksum", "scores");
	}
	@Override
	public String description() {
		return "Compute consistency checksum of similarity scores.";
	}
	private ScoreStats checksum(Dataset dataset) {
		return Cache.get(ScoreStats.class, Paths.get("checksums", "scores"), dataset.path(), () -> {
			var trio = new QuantileTrio(dataset);
			var stats = new ScoreStats();
			stats.matching = trio.matching.average();
			stats.nonmatching = trio.nonmatching.average();
			stats.selfmatching = trio.selfmatching.average();
			var hash = new Hasher();
			for (var row : ScoreCache.load(dataset)) {
				for (var score : row) {
					hash.add(score);
				}
			}
			stats.hash = hash.compute();
			return stats;
		});
	}
	private ScoreStats checksum(Profile profile) {
		return ScoreStats.sum(profile.datasets().parallelStream().map(this::checksum).toList());
	}
	public byte[] global() {
		return checksum(Profile.everything()).hash;
	}
	@Override
	public void run() {
		var table = new PrettyTable();
		for (var profile : Profile.all()) {
			MissingBaselineException.silence().run(() -> {
				var stats = checksum(profile);
				table.add("Dataset", profile.name());
				table.add("Matching", Pretty.decibans(stats.matching, profile.name(), "matching"));
				table.add("Non-matching", Pretty.decibans(stats.nonmatching, profile.name(), "nonmatching"));
				table.add("Self-matching", Pretty.decibans(stats.selfmatching, profile.name(), "selfmatching"));
				table.add("Hash", Pretty.hash(stats.hash, profile.name(), "hash"));
			});
		}
		table.print();
	}
}
