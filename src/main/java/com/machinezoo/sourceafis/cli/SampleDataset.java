// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.commons.lang3.tuple.*;
import one.util.streamex.*;

class SampleDataset {
	final String name;
	final SampleDownload.Format format;
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
	private SampleDataset(String name, SampleDownload.Format format) {
		this.name = name;
		this.format = format;
		dpi = dpi(name);
		layout = new SampleLayout(SampleDownload.unpack(name, format));
	}
	private static final ConcurrentMap<Pair<String, SampleDownload.Format>, SampleDataset> all = new ConcurrentHashMap<>();
	static SampleDataset get(String name, SampleDownload.Format format) {
		return all.computeIfAbsent(Pair.of(name, format), p -> new SampleDataset(p.getLeft(), p.getRight()));
	}
	static List<SampleDataset> all(SampleDownload.Format format) {
		return StreamEx.of(SampleDownload.AVAILABLE).map(n -> get(n, format)).toList();
	}
	static List<SampleDataset> all() {
		return all(SampleDownload.DEFAULT_FORMAT);
	}
	List<SampleFingerprint> fingerprints() {
		return IntStreamEx.range(layout.fingerprints()).mapToObj(n -> new SampleFingerprint(this, n)).toList();
	}
	Path path() {
		return Paths.get(name);
	}
}
