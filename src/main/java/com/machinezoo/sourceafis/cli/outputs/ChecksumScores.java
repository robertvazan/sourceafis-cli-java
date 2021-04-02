// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.*;
import java.nio.file.*;
import java.util.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class ChecksumScores {
	private static class QFunctionStats {
		long count;
		double average;
	}
	private static class Stats {
		QFunctionStats matching;
		QFunctionStats nonmatching;
		QFunctionStats selfmatching;
		byte[] hash;
	};
	private static QFunctionStats average(double[] function) {
		var stats = new QFunctionStats();
		stats.count = function.length;
		stats.average = Arrays.stream(function).average().getAsDouble();
		return stats;
	}
	private static Stats checksum(Dataset dataset) {
		return Cache.get(Stats.class, Paths.get("checksums", "scores"), dataset.path(), () -> {
			var trio = QuantileFunction.of(dataset);
			var stats = new Stats();
			stats.matching = average(trio.matching);
			stats.nonmatching = average(trio.nonmatching);
			stats.selfmatching = average(trio.selfmatching);
			var buffer = new byte[8];
			var hash = new Hash();
			for (var row : Scores.of(dataset)) {
				for (var score : row) {
					ByteBuffer.wrap(buffer).putDouble(score);
					hash.add(buffer);
				}
			}
			stats.hash = hash.compute();
			return stats;
		});
	}
	private static QFunctionStats sum(List<QFunctionStats> list) {
		var sum = new QFunctionStats();
		for (var stats : list) {
			sum.count += stats.count;
			sum.average += stats.count * stats.average;
		}
		sum.average /= sum.count;
		return sum;
	}
	private static Stats checksum(Profile profile) {
		var partial = StreamEx.of(profile.datasets).map(ds -> checksum(ds)).toList();
		var sum = new Stats();
		sum.matching = sum(StreamEx.of(partial).map(s -> s.matching).toList());
		sum.nonmatching = sum(StreamEx.of(partial).map(s -> s.nonmatching).toList());
		sum.selfmatching = sum(StreamEx.of(partial).map(s -> s.selfmatching).toList());
		var hash = new Hash();
		for (var stats : partial)
			hash.add(stats.hash);
		sum.hash = hash.compute();
		return sum;
	}
	public static byte[] global() {
		return checksum(Profile.everything()).hash;
	}
	public static void report() {
		var table = new Pretty.Table("Dataset", "Matching", "Non-matching", "Self-matching", "Hash");
		for (var profile : Profile.all()) {
			var stats = checksum(profile);
			table.add(
				profile.name,
				Pretty.decibans(stats.matching.average, profile.name, "matching"),
				Pretty.decibans(stats.nonmatching.average, profile.name, "nonmatching"),
				Pretty.decibans(stats.selfmatching.average, profile.name, "selfmatching"),
				Pretty.hash(stats.hash, profile.name, "hash"));
		}
		Pretty.print(table.format());
	}
}
