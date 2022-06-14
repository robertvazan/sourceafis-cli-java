// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

import java.util.*;

public class SingleSample extends SampleProfile {
	public final Sample sample;
	public SingleSample(Sample sample) {
		this.sample = sample;
	}
	@Override
	public String name() {
		return sample.name;
	}
	@Override
	public List<Sample> samples() {
		return List.of(sample);
	}
	@Override
	public boolean equals(Object obj) {
		return obj instanceof SingleSample && sample == ((SingleSample)obj).sample;
	}
	@Override
	public int hashCode() {
		return sample.hashCode();
	}
}
