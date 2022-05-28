// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;

public record TransparencyStats(
	String mime,
	int count,
	long length,
	long normalized,
	byte[] hash) {
	public static TransparencyStats of(String mime, byte[] data) {
		var normalized = Serializer.normalize(mime, data);
		return new TransparencyStats(
			mime,
			1,
			data.length,
			normalized.length,
			Hasher.hash(normalized));
	}
	public static TransparencyStats sum(List<TransparencyStats> list) {
		return new TransparencyStats(
			list.get(0).mime,
			Stats.sumAsInt(list, s -> s.count),
			Stats.sumAsLong(list, s -> s.length),
			Stats.sumAsLong(list, s -> s.normalized),
			Stats.sumHash(list, s -> s.hash));
	}
}
