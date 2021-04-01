// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class ChecksumTemplates {
	private static class Stats {
		int count;
		long length;
		byte[] hash;
	};
	private static Stats checksum(Fingerprint fp) {
		return Cache.get(Stats.class, Paths.get("checksums", "templates"), fp.path(), () -> {
			var checksum = new Stats();
			var serialized = Template.serialized(fp);
			checksum.count = 1;
			checksum.length = serialized.length;
			checksum.hash = Hash.of(Serializer.normalize(serialized));
			return checksum;
		});
	}
	private static Stats checksum(Profile profile) {
		var sum = new Stats();
		var hash = new Hash();
		for (var fp : profile.fingerprints()) {
			var stats = checksum(fp);
			sum.count += stats.count;
			sum.length += stats.length;
			hash.add(stats.hash);
		}
		sum.hash = hash.compute();
		return sum;
	}
	public static void report() {
		var table = new Pretty.Table("Dataset", "Length", "Total", "Hash");
		for (var profile : Profile.all()) {
			var stats = checksum(profile);
			table.add(profile.name, Pretty.length(stats.length / stats.count), Pretty.length(stats.length), Pretty.hash(stats.hash));
		}
		Pretty.print(table.format());
	}
}
