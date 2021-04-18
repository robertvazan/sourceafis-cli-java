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
		var hash = new Hash();
		for (var stats : list) {
			sum.count += stats.count;
			sum.length += stats.length;
			sum.normalized += stats.normalized;
			hash.add(stats.hash);
		}
		sum.hash = hash.compute();
		return sum;
	}
}
