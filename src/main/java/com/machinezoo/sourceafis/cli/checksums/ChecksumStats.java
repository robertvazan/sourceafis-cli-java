// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;

public record ChecksumStats(
	String mime,
	long count,
	long length,
	long normalized,
	byte[] hash) {
	public static ChecksumStats of(String mime, byte[] data) {
		var normalized = Serializer.normalize(mime, data);
		return new ChecksumStats(
			mime,
			1,
			data.length,
			normalized.length,
			Hasher.hash(normalized));
	}
	public static ChecksumStats sum(List<ChecksumStats> list) {
		return new ChecksumStats(
			list.get(0).mime,
			Stats.sum(list, s -> s.count),
			Stats.sum(list, s -> s.length),
			Stats.sum(list, s -> s.normalized),
			Stats.hash(list, s -> s.hash));
	}
}
