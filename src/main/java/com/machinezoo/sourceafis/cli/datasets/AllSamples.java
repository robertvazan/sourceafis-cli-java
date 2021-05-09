// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

import java.util.*;

public class AllSamples extends SampleProfile {
	@Override
	public String name() {
		return "All";
	}
	@Override
	public List<Sample> samples() {
		return List.of(Sample.values());
	}
	@Override
	public boolean equals(Object obj) {
		return obj instanceof AllSamples;
	}
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
