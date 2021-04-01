// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import java.util.*;
import org.slf4j.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class ChecksumTemplates {
	public static class Stats {
		public int count;
		public int size;
		public byte[] hash;
	};
	public static Stats checksum(Fingerprint fp) {
		return Cache.get(Stats.class, Paths.get("checksums", "templates"), fp.path(), () -> {
			var checksum = new Stats();
			var serialized = Template.serialized(fp);
			checksum.count = 1;
			checksum.size = serialized.length;
			checksum.hash = Hash.of(Serializer.normalize(serialized));
			return checksum;
		});
	}
	public static Stats sum(List<Stats> list) {
		var sum = new Stats();
		var hash = new Hash();
		for (var footprint : list) {
			sum.count += footprint.count;
			sum.size += footprint.size;
			hash.add(footprint.hash);
		}
		sum.hash = hash.compute();
		return sum;
	}
	public static Stats sum() {
		return sum(StreamEx.of(Fingerprint.all()).map(fp -> checksum(fp)).toList());
	}
	private static final Logger logger = LoggerFactory.getLogger(ChecksumTemplates.class);
	public static void report() {
		var sum = sum();
		logger.info("Template hash: {}", Pretty.hash(sum.hash));
	}
}
