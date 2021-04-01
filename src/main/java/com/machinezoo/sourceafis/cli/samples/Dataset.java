// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.samples;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.commons.lang3.tuple.*;
import one.util.streamex.*;

public class Dataset {
	public final String name;
	public final Download.Format format;
	public final double dpi;
	public final DatasetLayout layout;
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
	private Dataset(String name, Download.Format format) {
		this.name = name;
		this.format = format;
		dpi = dpi(name);
		layout = new DatasetLayout(Download.unpack(name, format));
	}
	private static final ConcurrentMap<Pair<String, Download.Format>, Dataset> all = new ConcurrentHashMap<>();
	public static Dataset get(String name, Download.Format format) {
		return all.computeIfAbsent(Pair.of(name, format), p -> new Dataset(p.getLeft(), p.getRight()));
	}
	public static List<Dataset> all(Download.Format format) {
		return StreamEx.of(Download.DATASETS).map(n -> get(n, format)).toList();
	}
	public static List<Dataset> all() {
		return all(Download.DEFAULT_FORMAT);
	}
	public List<Fingerprint> fingerprints() {
		return IntStreamEx.range(layout.fingerprints()).mapToObj(n -> new Fingerprint(this, n)).toList();
	}
	public Path path() {
		return Paths.get(name);
	}
}