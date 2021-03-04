// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.nio.file.*;
import com.machinezoo.noexception.*;

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
	byte[] load() {
		return Exceptions.sneak().get(() -> Files.readAllBytes(SampleDownload.directory(dataset.name).resolve(dataset.layout.filename(id))));
	}
}
