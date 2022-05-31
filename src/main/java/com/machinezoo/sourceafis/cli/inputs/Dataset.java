// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.inputs;

import java.nio.file.*;
import java.util.*;
import one.util.streamex.*;

public enum Dataset {
	FVC2000_1B("fvc2000-1b"),
	FVC2000_2B("fvc2000-2b"),
	FVC2000_3B("fvc2000-3b"),
	FVC2000_4B("fvc2000-4b"),
	FVC2002_1B("fvc2002-1b"),
	FVC2002_2B("fvc2002-2b"),
	FVC2002_3B("fvc2002-3b"),
	FVC2002_4B("fvc2002-4b"),
	FVC2004_1B("fvc2004-1b"),
	FVC2004_2B("fvc2004-2b"),
	FVC2004_3B("fvc2004-3b"),
	FVC2004_4B("fvc2004-4b");
	private final String codename;
	Dataset(String codename) {
		this.codename = codename;
	}
	public String codename() {
		return codename;
	}
	public double dpi() {
		return switch (this) {
			case FVC2002_2B -> 569;
			case FVC2004_3B -> 512;
			default -> 500;
		};
	}
	public static List<Dataset> all() {
		return List.of(Dataset.values());
	}
	public DatasetLayout layout() {
		return DatasetLayout.get(this);
	}
	public List<Fingerprint> fingerprints() {
		return IntStreamEx.range(layout().fingerprints()).mapToObj(n -> new Fingerprint(this, n)).toList();
	}
	public Path path() {
		return Paths.get(codename());
	}
}
