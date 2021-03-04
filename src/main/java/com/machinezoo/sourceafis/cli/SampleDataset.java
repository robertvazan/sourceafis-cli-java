// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.util.*;
import java.util.concurrent.*;
import one.util.streamex.*;

class SampleDataset {
	final String name;
	final double dpi;
	final SampleLayout layout;
	private static double dpi(String dataset) {
		switch (dataset) {
		case "fvc2002-2b":
			return 569;
		case "fvc2004-3b":
			return 512;
		default:
			return 500;
		}
	}
	private SampleDataset(String name) {
		this.name = name;
		dpi = dpi(name);
		layout = SampleLayout.scan(name);
	}
	private static final ConcurrentMap<String, SampleDataset> all = new ConcurrentHashMap<>();
	static SampleDataset get(String name) {
		return all.computeIfAbsent(name, SampleDataset::new);
	}
	static List<SampleDataset> all() {
		return StreamEx.of(SampleDownload.AVAILABLE).map(n -> get(n)).toList();
	}
	List<SampleFingerprint> fingerprints() {
		return IntStreamEx.range(layout.fingerprints()).mapToObj(n -> new SampleFingerprint(this, n)).toList();
	}
}
