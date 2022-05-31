// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.inputs;

import java.nio.file.*;
import java.util.*;

public record Fingerprint(Dataset dataset, int id) {
	public String name() {
		return dataset.layout().name(id);
	}
	public Path path() {
		return dataset.path().resolve(name());
	}
	public Finger finger() {
		return new Finger(dataset, dataset.layout().finger(id));
	}
	public static List<Fingerprint> all() {
		return Profile.everything().fingerprints();
	}
}
