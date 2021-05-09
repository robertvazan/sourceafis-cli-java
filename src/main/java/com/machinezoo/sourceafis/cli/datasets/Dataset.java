// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

import java.nio.file.*;
import java.util.*;
import org.apache.commons.lang3.builder.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;
import one.util.streamex.*;

public class Dataset implements DataIdentifier {
	public final Sample sample;
	public final ImageFormat format;
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
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Dataset))
			return false;
		var other = (Dataset)obj;
		return new EqualsBuilder()
			.append(sample, other.sample)
			.append(format, other.format)
			.isEquals();
	}
	@Override
	public int hashCode() {
		return Objects.hash(sample, format);
	}
}
