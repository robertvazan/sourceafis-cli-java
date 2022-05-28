// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;

public record TemplateStats(
	int count,
	long length,
	long normalized,
	byte[] hash) {
	public static TemplateStats sum(List<TemplateStats> list) {
		return new TemplateStats(
			Stats.sumAsInt(list, s -> s.count),
			Stats.sumAsLong(list, s -> s.length),
			Stats.sumAsLong(list, s -> s.normalized),
			Stats.sumHash(list, s -> s.hash));
	}
}
