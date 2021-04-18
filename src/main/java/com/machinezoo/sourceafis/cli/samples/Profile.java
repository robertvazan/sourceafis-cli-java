// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.samples;

import java.util.*;
import one.util.streamex.*;

public class Profile {
	public final String name;
	public final List<Dataset> datasets;
	private Profile(String name, List<Dataset> datasets) {
		this.name = name;
		this.datasets = datasets;
	}
	private static Profile everything(ImageFormat format) {
		return new Profile("All", Dataset.all(format));
	}
	public static Profile everything() {
		return everything(ImageFormat.DEFAULT);
	}
	private static Profile best(ImageFormat format) {
		return new Profile("High quality", StreamEx.of("fvc2002-1b", "fvc2002-2b").map(n -> Dataset.get(n, format)).toList());
	}
	public static Profile best() {
		return best(ImageFormat.DEFAULT);
	}
	private static List<Profile> aggregate(ImageFormat format) {
		return List.of(best(format), everything(format));
	}
	public static List<Profile> aggegate() {
		return aggregate(ImageFormat.DEFAULT);
	}
	public static List<Profile> all(ImageFormat format) {
		return StreamEx.of(Dataset.all(format))
			.map(ds -> new Profile(ds.name, List.of(ds)))
			.append(aggregate(format))
			.toList();
	}
	public static List<Profile> all() {
		return all(ImageFormat.DEFAULT);
	}
	public List<Fingerprint> fingerprints() {
		return StreamEx.of(datasets).flatCollection(ds -> ds.fingerprints()).toList();
	}
}
