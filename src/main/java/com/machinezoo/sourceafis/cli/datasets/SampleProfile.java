// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

import java.util.*;
import one.util.streamex.*;

public interface SampleProfile {
	String name();
	List<Sample> samples();
	static List<SampleProfile> aggregate() {
		return List.of(new BestSamples(), new AllSamples());
	}
	static List<SampleProfile> all() {
		return StreamEx.of(Sample.values()).map(s -> (SampleProfile)new SingleSample(s)).append(aggregate()).toList();
	}
}
