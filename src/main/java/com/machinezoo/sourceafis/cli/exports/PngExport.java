// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.exports;

import java.io.*;
import java.nio.file.*;
import javax.imageio.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class PngExport implements Runnable {
	@Override
	public void run() {
		var category = Paths.get("exports", "png");
		for (var dataset : Dataset.all(ImageFormat.ORIGINAL)) {
			for (var fp : dataset.fingerprints()) {
				Cache.get(byte[].class, category, Cache.withExtension(fp.path(), ".png"), () -> {
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
		Pretty.print("Saved: " + Pretty.dump(category));
	}
}
