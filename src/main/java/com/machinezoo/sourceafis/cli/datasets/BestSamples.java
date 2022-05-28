// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

import java.util.*;

public record BestSamples() implements SampleProfile {
	@Override
	public String name() {
		return "High quality";
	}
	@Override
	public List<Sample> samples() {
		return List.of(Sample.FVC2002_1B, Sample.FVC2002_2B);
	}
}
