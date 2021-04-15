// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.samples;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;
import one.util.streamex.*;

public class Fingerprint implements DataIdentifier {
	public final Dataset dataset;
	public final int id;
	public Fingerprint(Dataset dataset, int id) {
		this.dataset = dataset;
		this.id = id;
	}
	public String name() {
		return dataset.layout.name(id);
	}
	@Override
	public Path path() {
		return dataset.path().resolve(name());
	}
	public Finger finger() {
		return new Finger(dataset, dataset.layout.finger(id));
	}
	public static List<Fingerprint> all() {
		return StreamEx.of(Dataset.all()).flatCollection(ds -> ds.fingerprints()).toList();
	}
	public byte[] load() {
		return Exceptions.sneak().get(() -> Files.readAllBytes(dataset.layout.directory.resolve(dataset.layout.filename(id))));
	}
	public FingerprintImage decode() {
		var options = new FingerprintImageOptions().dpi(dataset.dpi);
		if (dataset.format == ImageFormat.GRAY) {
			var gray = load();
			int width = (Byte.toUnsignedInt(gray[0]) << 8) | Byte.toUnsignedInt(gray[1]);
			int height = (Byte.toUnsignedInt(gray[2]) << 8) | Byte.toUnsignedInt(gray[3]);
			return new FingerprintImage(width, height, Arrays.copyOfRange(gray, 4, gray.length), options);
		} else
			return new FingerprintImage(load(), options);
	}
}
