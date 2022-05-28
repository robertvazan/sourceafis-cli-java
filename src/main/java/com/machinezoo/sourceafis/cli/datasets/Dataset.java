// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;
import one.util.streamex.*;

public record Dataset(Sample sample, ImageFormat format) implements DataIdentifier {
	public String name() {
		return sample.name;
	}
	public Dataset(Sample sample, ImageFormat format) {
		this.sample = sample;
		this.format = format;
	}
	public static List<Dataset> all() {
		return StreamEx.of(Sample.values()).map(s -> new Dataset(s, ImageFormat.DEFAULT)).toList();
	}
	public DatasetLayout layout() {
		return DatasetLayout.get(this);
	}
	public List<Fingerprint> fingerprints() {
		return IntStreamEx.range(layout().fingerprints()).mapToObj(n -> new Fingerprint(this, n)).toList();
	}
	@Override
	public Path path() {
		return Paths.get(name());
	}
}
