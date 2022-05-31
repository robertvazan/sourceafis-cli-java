// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.inputs;

import java.nio.file.*;

public record FingerprintPair(Dataset dataset, int probeId, int candidateId) {
	public FingerprintPair(Fingerprint probe, Fingerprint candidate) {
		this(probe.dataset(), probe.id(), candidate.id());
		if (!probe.dataset().equals(candidate.dataset()))
			throw new IllegalArgumentException();
	}
	public Fingerprint probe() {
		return new Fingerprint(dataset, probeId);
	}
	public Fingerprint candidate() {
		return new Fingerprint(dataset, candidateId);
	}
	public Path path() {
		return probe().path().resolve(candidate().name());
	}
}
