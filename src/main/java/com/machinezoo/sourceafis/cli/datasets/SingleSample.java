// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

import java.util.*;

public record SingleSample(Sample sample) implements SampleProfile {
	@Override
	public String name() {
		return sample.name;
	}
	@Override
	public List<Sample> samples() {
		return List.of(sample);
	}
}
