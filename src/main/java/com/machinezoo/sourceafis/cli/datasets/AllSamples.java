// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

import java.util.*;

public record AllSamples() implements SampleProfile {
	@Override
	public String name() {
		return "All";
	}
	@Override
	public List<Sample> samples() {
		return List.of(Sample.values());
	}
}
