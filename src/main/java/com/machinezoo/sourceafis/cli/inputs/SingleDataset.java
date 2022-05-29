// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.inputs;

import java.util.*;

public record SingleDataset(Dataset dataset) implements Profile {
	@Override
	public String name() {
		return dataset.codename();
	}
	@Override
	public List<Dataset> datasets() {
		return List.of(dataset);
	}
}
