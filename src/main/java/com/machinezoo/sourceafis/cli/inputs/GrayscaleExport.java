// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.inputs;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import javax.imageio.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.cli.config.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;

public class GrayscaleExport extends Command {
	@Override
	public List<String> subcommand() {
		return List.of("export", "grayscale");
	}
	@Override
	public String description() {
		return "Convert sample images to grayscale.";
	}
	private static final Pattern PATTERN = Pattern.compile("(.+)_[0-9]+\\.(?:tif|tiff|png|bmp|jpg|jpeg)");
	@Override
	public void run() {
		var category = Paths.get("exports", "grayscale");
		for (var dataset : Dataset.all()) {
			var outdir = Configuration.output().resolve(category).resolve(dataset.codename());
			var marker = outdir.resolve("done");
			if (!Files.exists(marker)) {
				Exceptions.sneak().run(() -> Files.createDirectories(outdir));
				var indir = new ImageDownload(dataset).unpack();
				for (var input : Exceptions.sneak().get(() -> Files.list(indir).toList())) {
					var filename = input.getFileName().toString();
					var matcher = PATTERN.matcher(filename);
					if (matcher.matches()) {
						var output = outdir.resolve(indir.relativize(input)).resolveSibling(input.getFileName() + ".gray");
						var buffered = Exceptions.sneak().get(() -> ImageIO.read(new ByteArrayInputStream(Files.readAllBytes(input))));
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
						Exceptions.sneak().run(() -> Files.write(output, gray));
					}
				}
				Exceptions.sneak().run(() -> Files.createFile(marker));
			}
		}
		Pretty.print("Saved: " + Pretty.dump(category));
	}
}
