// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;

public record TemplateStats(
	long count,
	long length,
	long normalized,
	byte[] hash) {
	public static TemplateStats sum(List<TemplateStats> list) {
		return new TemplateStats(
			Stats.sum(list, s -> s.count),
			Stats.sum(list, s -> s.length),
			Stats.sum(list, s -> s.normalized),
			Stats.hash(list, s -> s.hash));
	}
}
