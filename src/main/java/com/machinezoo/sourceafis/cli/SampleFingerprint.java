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
		return Exceptions.sneak().get(() -> Files.readAllBytes(dataset.layout.directory.resolve(dataset.layout.filename(id))));
	}
	FingerprintImage decode() {
		if (dataset.format == SampleDownload.Format.GRAY) {
			var gray = load();
			int width = (Byte.toUnsignedInt(gray[0]) << 8) | Byte.toUnsignedInt(gray[1]);
			int height = (Byte.toUnsignedInt(gray[2]) << 8) | Byte.toUnsignedInt(gray[3]);
			return new FingerprintImage()
				.dpi(dataset.dpi)
				.grayscale(width, height, Arrays.copyOfRange(gray, 4, gray.length));
		} else {
			return new FingerprintImage()
				.dpi(dataset.dpi)
				.decode(load());
		}
	}
}
