// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.inputs;

import java.util.*;
import one.util.streamex.*;

public record Finger(Dataset dataset, int id) {
	public List<Fingerprint> fingerprints() {
		var layout = dataset.layout();
		return IntStreamEx.range(layout.impressions(id))
			.mapToObj(n -> new Fingerprint(dataset, layout.fingerprint(id, n)))
			.toList();
	}
}
