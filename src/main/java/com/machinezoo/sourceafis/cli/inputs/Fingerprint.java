// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.inputs;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public record Fingerprint(Dataset dataset, int id) implements DataIdentifier {
	public String name() {
		return dataset.layout().name(id);
	}
	@Override
	public Path path() {
		return dataset.path().resolve(name());
	}
	public Finger finger() {
		return new Finger(dataset, dataset.layout().finger(id));
	}
	public static List<Fingerprint> all() {
		return Profile.everything().fingerprints();
	}
	public byte[] load() {
		var layout = dataset.layout();
		return Exceptions.sneak().get(() -> Files.readAllBytes(layout.directory.resolve(layout.filename(id))));
	}
	public FingerprintImage decode() {
		var options = new FingerprintImageOptions().dpi(dataset.dpi());
		var gray = load();
		int width = (Byte.toUnsignedInt(gray[0]) << 8) | Byte.toUnsignedInt(gray[1]);
		int height = (Byte.toUnsignedInt(gray[2]) << 8) | Byte.toUnsignedInt(gray[3]);
		return new FingerprintImage(width, height, Arrays.copyOfRange(gray, 4, gray.length), options);
	}
}
