// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.nio.*;
import java.nio.file.*;
import java.util.*;
import com.machinezoo.sourceafis.cli.benchmarks.*;
import com.machinezoo.sourceafis.cli.datasets.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;
import one.util.streamex.*;

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
			var buffer = new byte[8];
			var hash = new Hasher();
			for (var row : ScoreCache.load(dataset)) {
				for (var score : row) {
					ByteBuffer.wrap(buffer).putDouble(score);
					hash.add(buffer);
				}
			}
			stats.hash = hash.compute();
			return stats;
		});
	}
	private ScoreStats checksum(Profile profile) {
		return ScoreStats.sum(StreamEx.of(profile.datasets()).map(this::checksum).toList());
	}
	public byte[] global() {
		return checksum(Profile.everything()).hash;
	}
	@Override
	public void run() {
		var table = new PrettyTable("Dataset", "Matching", "Non-matching", "Self-matching", "Hash");
		for (var profile : Profile.all()) {
			var stats = checksum(profile);
			table.add(
				profile.name(),
				Pretty.decibans(stats.matching, profile.name(), "matching"),
				Pretty.decibans(stats.nonmatching, profile.name(), "nonmatching"),
				Pretty.decibans(stats.selfmatching, profile.name(), "selfmatching"),
				Pretty.hash(stats.hash, profile.name(), "hash"));
		}
		Pretty.print(table.format());
	}
}
