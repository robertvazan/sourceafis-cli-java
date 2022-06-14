// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

import java.util.*;
import one.util.streamex.*;

public abstract class SampleProfile {
	public abstract String name();
	public abstract List<Sample> samples();
	public static List<SampleProfile> aggregate() {
		return List.of(new BestSamples(), new AllSamples());
	}
	public static List<SampleProfile> all() {
		return StreamEx.of(Sample.values()).map(s -> (SampleProfile)new SingleSample(s)).append(aggregate()).toList();
	}
}
