// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class TemplateStats {
	public int count;
	public long length;
	public long normalized;
	public byte[] hash;
	public static TemplateStats sum(List<TemplateStats> list) {
		var sum = new TemplateStats();
		sum.count = Stats.sumAsInt(list, s -> s.count);
		sum.length = Stats.sumAsLong(list, s -> s.length);
		sum.normalized = Stats.sumAsLong(list, s -> s.normalized);
		sum.hash = Stats.sumHash(list, s -> s.hash);
		return sum;
	}
}
