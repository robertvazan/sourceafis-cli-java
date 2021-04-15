// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.samples;

import java.nio.file.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class FingerprintPair implements DataIdentifier {
	public final Dataset dataset;
	public final int probeId;
	public final int candidateId;
	public FingerprintPair(Fingerprint probe, Fingerprint candidate) {
		if (!probe.dataset.name.equals(candidate.dataset.name))
			throw new IllegalArgumentException();
		this.dataset = probe.dataset;
		this.probeId = probe.id;
		this.candidateId = candidate.id;
	}
	public Fingerprint probe() {
		return new Fingerprint(dataset, probeId);
	}
	public Fingerprint candidate() {
		return new Fingerprint(dataset, candidateId);
	}
	@Override
	public Path path() {
		return probe().path().resolve(candidate().name());
	}
}
