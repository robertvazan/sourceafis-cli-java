// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

import java.nio.file.*;
import java.util.*;
import org.apache.commons.lang3.builder.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class FingerprintPair implements DataIdentifier {
	public final Dataset dataset;
	public final int probeId;
	public final int candidateId;
	public FingerprintPair(Fingerprint probe, Fingerprint candidate) {
		if (!probe.dataset.equals(candidate.dataset))
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
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FingerprintPair))
			return false;
		var other = (FingerprintPair)obj;
		return new EqualsBuilder()
			.append(dataset, other.dataset)
			.append(probeId, other.probeId)
			.append(candidateId, other.candidateId)
			.isEquals();
	}
	@Override
	public int hashCode() {
		return Objects.hash(dataset, probeId, candidateId);
	}
}
