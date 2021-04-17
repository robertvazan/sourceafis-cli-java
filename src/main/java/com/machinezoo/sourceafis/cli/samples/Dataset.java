// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.samples;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.commons.lang3.builder.*;
import org.apache.commons.lang3.tuple.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;
import one.util.streamex.*;

public class Dataset implements DataIdentifier {
	public final String name;
	public final ImageFormat format;
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
	private Dataset(String name, ImageFormat format) {
		this.name = name;
		this.format = format;
		dpi = dpi(name);
		layout = new DatasetLayout(Download.unpack(name, format));
	}
	private static final ConcurrentMap<Pair<String, ImageFormat>, Dataset> all = new ConcurrentHashMap<>();
	public static Dataset get(String name, ImageFormat format) {
		return all.computeIfAbsent(Pair.of(name, format), p -> new Dataset(p.getLeft(), p.getRight()));
	}
	public static List<Dataset> all(ImageFormat format) {
		return StreamEx.of(Download.DATASETS).map(n -> get(n, format)).toList();
	}
	public static List<Dataset> all() {
		return all(ImageFormat.DEFAULT);
	}
	public List<Fingerprint> fingerprints() {
		return IntStreamEx.range(layout.fingerprints()).mapToObj(n -> new Fingerprint(this, n)).toList();
	}
	@Override
	public Path path() {
		return Paths.get(name);
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Dataset))
			return false;
		var other = (Dataset)obj;
		return new EqualsBuilder()
			.append(name, other.name)
			.append(format, other.format)
			.isEquals();
	}
	@Override
	public int hashCode() {
		return Objects.hash(name, format);
	}
}
