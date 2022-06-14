// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

import java.util.*;
import org.apache.commons.lang3.builder.*;
import one.util.streamex.*;

public class Finger {
	public final Dataset dataset;
	public final int id;
	public Finger(Dataset dataset, int id) {
		this.dataset = dataset;
		this.id = id;
	}
	public List<Fingerprint> fingerprints() {
		var layout = dataset.layout();
		return IntStreamEx.range(layout.impressions(id))
			.mapToObj(n -> new Fingerprint(dataset, layout.fingerprint(id, n)))
			.toList();
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Finger))
			return false;
		var other = (Finger)obj;
		return new EqualsBuilder()
			.append(dataset, other.dataset)
			.append(id, other.id)
			.isEquals();
	}
	@Override
	public int hashCode() {
		return Objects.hash(dataset, id);
	}
}
