// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.inputs;

import java.util.*;
import one.util.streamex.*;

public interface Profile {
	String name();
	List<Dataset> datasets();
	static Profile everything() {
		return new AllDatasets();
	}
	static List<Profile> aggregate() {
		return List.of(new BestDatasets(), new AllDatasets());
	}
	static List<Profile> all() {
		return StreamEx.of(Dataset.all()).map(ds -> (Profile)new SingleDataset(ds)).append(aggregate()).toList();
	}
	default List<Fingerprint> fingerprints() {
		return StreamEx.of(datasets()).flatCollection(ds -> ds.fingerprints()).toList();
	}
}
