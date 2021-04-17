// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.exports;

import java.io.*;
import java.nio.file.*;
import javax.imageio.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class GrayscaleExport implements Runnable {
	@Override
	public void run() {
		var category = Paths.get("exports", "grayscale");
		for (var dataset : Dataset.all(ImageFormat.ORIGINAL)) {
			for (var fp : dataset.fingerprints()) {
				Cache.get(byte[].class, category, Cache.withExtension(fp.path(), ".gray"), () -> {
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
		Pretty.print("Saved: " + Pretty.dump(category));
	}
}