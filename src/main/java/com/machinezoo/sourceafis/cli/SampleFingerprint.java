// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.*;
import one.util.streamex.*;

class SampleFingerprint {
	final SampleDataset dataset;
	final int id;
	SampleFingerprint(SampleDataset dataset, int id) {
		this.dataset = dataset;
		this.id = id;
	}
	String name() {
		return dataset.layout.name(id);
	}
	Path path() {
		return Paths.get(dataset.name, name());
	}
	static List<SampleFingerprint> all() {
		return StreamEx.of(SampleDataset.all()).flatCollection(ds -> ds.fingerprints()).toList();
	}
	byte[] load() {
		return Exceptions.sneak().get(() -> Files.readAllBytes(SampleDownload.directory(dataset.name).resolve(dataset.layout.filename(id))));
	}
	FingerprintImage decode() {
		return new FingerprintImage()
			.dpi(dataset.dpi)
			.decode(load());
	}
}
