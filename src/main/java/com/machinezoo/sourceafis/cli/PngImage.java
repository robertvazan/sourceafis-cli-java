// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.io.*;
import java.nio.file.*;
import javax.imageio.*;
import com.machinezoo.noexception.*;

class PngImage {
	static byte[] of(SampleFingerprint fp) {
		return PersistentCache.get(byte[].class, Paths.get("png"), PersistentCache.withExtension(fp.path(), ".png"), () -> {
			var buffered = Exceptions.sneak().get(() -> ImageIO.read(new ByteArrayInputStream(fp.load())));
			if (buffered == null)
				throw new IllegalArgumentException("Unsupported image format.");
			var output = new ByteArrayOutputStream();
			boolean success = Exceptions.sneak().getAsBoolean(() -> ImageIO.write(buffered, "PNG", output));
			if (!success)
				throw new IllegalStateException("PNG image writing is not supported.");
			return output.toByteArray();
		});
	}
	static void generate() {
		for (var fp : SampleFingerprint.all())
			of(fp);
	}
}
