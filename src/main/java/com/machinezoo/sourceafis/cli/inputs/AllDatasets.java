// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.inputs;

import java.util.*;

public record AllDatasets() implements Profile {
	@Override
	public String name() {
		return "All";
	}
	@Override
	public List<Dataset> datasets() {
		return Dataset.all();
	}
}
