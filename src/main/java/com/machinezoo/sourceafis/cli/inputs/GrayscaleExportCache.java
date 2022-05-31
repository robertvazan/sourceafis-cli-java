// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.inputs;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;
import javax.imageio.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public record GrayscaleExportCache(Dataset dataset) implements FileCache {
	@Override
	public Path category() {
		return Paths.get("exports", "grayscale");
	}
	@Override
	public Path sector() {
		return dataset.path();
	}
	private static final Pattern PATTERN = Pattern.compile(".+\\.(?:tif|tiff|png|bmp|jpg|jpeg)");
	@Override
	public void populate(CacheWriter<Path, byte[]> writer) {
		var indir=new ImageCache(dataset).load().directory();
		for (var inpath : Exceptions.sneak().get(() -> Files.list(indir).toList())) {
			var filename = inpath.getFileName().toString();
			var matcher = PATTERN.matcher(filename);
			if (matcher.matches()) {
				var outpath = indir.relativize(inpath).resolveSibling(inpath.getFileName() + ".gray");
				var buffered = Exceptions.sneak().get(() -> ImageIO.read(new ByteArrayInputStream(Files.readAllBytes(inpath))));
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
				writer.put(outpath, gray);
			}
		}
	}
}
