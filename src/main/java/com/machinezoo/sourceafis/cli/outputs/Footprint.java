// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import java.util.*;
import org.slf4j.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class Footprint {
	public static class Stats {
		public int count;
		public int serialized;
	}
	public static Stats measure(Fingerprint fp) {
		return Cache.get(Stats.class, Paths.get("footprints"), fp.path(), () -> {
			var footprint = new Stats();
			var serialized = Template.serialized(fp);
			footprint.count = 1;
			footprint.serialized = serialized.length;
			return footprint;
		});
	}
	public static Stats sum(List<Stats> list) {
		var sum = new Stats();
		for (var footprint : list) {
			sum.count += footprint.count;
			sum.serialized += footprint.serialized;
		}
		return sum;
	}
	public static Stats sum() {
		return sum(StreamEx.of(Fingerprint.all()).map(fp -> measure(fp)).toList());
	}
	private static final Logger logger = LoggerFactory.getLogger(Footprint.class);
	public static void report() {
		var sum = sum();
		logger.info("Template footprint: {} B serialized", sum.serialized / sum.count);
	}
}
