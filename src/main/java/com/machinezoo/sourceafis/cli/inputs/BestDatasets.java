// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.inputs;

import java.util.*;

public record BestDatasets() implements Profile {
	@Override
	public String name() {
		return "High quality";
	}
	@Override
	public List<Dataset> datasets() {
		return List.of(Dataset.FVC2002_1B, Dataset.FVC2002_2B);
	}
}
