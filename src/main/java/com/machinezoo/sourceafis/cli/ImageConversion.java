// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.io.*;
import java.nio.file.*;
import javax.imageio.*;
import com.machinezoo.noexception.*;

class ImageConversion {
	static void png() {
		for (var dataset : SampleDataset.all(SampleDownload.Format.ORIGINAL)) {
			for (var fp : dataset.fingerprints()) {
				PersistentCache.get(byte[].class, Paths.get("png"), PersistentCache.withExtension(fp.path(), ".png"), () -> {
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
	static void gray() {
		for (var dataset : SampleDataset.all(SampleDownload.Format.ORIGINAL)) {
			for (var fp : dataset.fingerprints()) {
				PersistentCache.get(byte[].class, Paths.get("gray"), PersistentCache.withExtension(fp.path(), ".gray"), () -> {
					var buffered = Exceptions.sneak().get(() -> ImageIO.read(new ByteArrayInputStream(fp.load())));
					if (buffered == null)
						throw new IllegalArgumentException("Unsupported image format.");
					int width = buffered.getWidth();
					int height = buffered.getHeight();
					int[] pixels = new int[width * height];
					buffered.getRGB(0, 0, width, height, pixels, 0, width);
					byte[] gray = new byte[4 + pixels.length];
					gray[0] = (byte)(width >> 8);
					gray[1] = (byte)width;
					gray[2] = (byte)(height >> 8);
					gray[3] = (byte)height;
					for (int i = 0; i < pixels.length; ++i) {
						int pixel = pixels[i];
						int color = (pixel & 0xff) + ((pixel >> 8) & 0xff) + ((pixel >> 16) & 0xff);
						gray[i + 4] = (byte)(color / 3);
					}
					return gray;
				});
			}
		}
	}
}
