// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.samples;

import java.util.*;
import org.apache.commons.lang3.builder.*;

public class Finger {
	public final Dataset dataset;
	public final int id;
	public Finger(Dataset dataset, int id) {
		this.dataset = dataset;
		this.id = id;
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
