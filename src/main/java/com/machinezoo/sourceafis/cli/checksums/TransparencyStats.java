// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class TransparencyStats {
	public String mime;
	public int count;
	public long length;
	public long normalized;
	public byte[] hash;
	public static TransparencyStats of(String mime, byte[] data) {
		var stats = new TransparencyStats();
		stats.mime = mime;
		stats.count = 1;
		stats.length = data.length;
		var normalized = Serializer.normalize(mime, data);
		stats.normalized = normalized.length;
		stats.hash = Hasher.hash(normalized);
		return stats;
	}
	public static TransparencyStats sum(List<TransparencyStats> list) {
		var sum = new TransparencyStats();
		sum.mime = list.get(0).mime;
		sum.count = Stats.sumAsInt(list, s -> s.count);
		sum.length = Stats.sumAsLong(list, s -> s.length);
		sum.normalized = Stats.sumAsLong(list, s -> s.normalized);
		sum.hash = Stats.sumHash(list, s -> s.hash);
		return sum;
	}
}
