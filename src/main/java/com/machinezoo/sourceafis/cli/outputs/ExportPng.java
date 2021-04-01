// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.io.*;
import java.nio.file.*;
import javax.imageio.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class ExportPng {
	public static void export() {
		for (var dataset : Dataset.all(Download.Format.ORIGINAL)) {
			for (var fp : dataset.fingerprints()) {
				Cache.get(byte[].class, Paths.get("exports", "png"), Cache.withExtension(fp.path(), ".png"), () -> {
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
		}
	}
}
