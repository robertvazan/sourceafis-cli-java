// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.inputs;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public record DecodedImageCache(Dataset dataset) {
	public LoadedCache<Fingerprint, FingerprintImage> load() {
		var grays = new GrayscaleImageCache(dataset).load();
		var layout = dataset.layout();
		return new LoadedCache<>() {
			@Override
			public Path directory() {
				return grays.directory();
			}
			@Override
			public FingerprintImage get(Fingerprint fp) {
				var options = new FingerprintImageOptions().dpi(dataset.dpi());
				var gray = grays.get(Paths.get(layout.filename(fp.id())));
				int width = (Byte.toUnsignedInt(gray[0]) << 8) | Byte.toUnsignedInt(gray[1]);
				int height = (Byte.toUnsignedInt(gray[2]) << 8) | Byte.toUnsignedInt(gray[3]);
				return new FingerprintImage(width, height, Arrays.copyOfRange(gray, 4, gray.length), options);
			}
		};
	}
}
