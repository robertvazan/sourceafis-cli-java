// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.inputs;

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpClient.*;
import java.nio.file.*;
import java.util.zip.*;
import org.apache.commons.io.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.cli.config.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public interface DownloadCache extends FileCache {
	String url();
	@Override
	default Path root() {
		return Configuration.home.resolve("inputs");
	}
	@Override
	default String action() {
		return "Downloading";
	}
	static final HttpClient client = HttpClient.newBuilder()
		.followRedirects(Redirect.NORMAL)
		.build();
	@Override
	default void populate(CacheWriter<Path, byte[]> writer) {
		Exceptions.sneak().run(() -> {
			try (	InputStream stream = client.send(HttpRequest.newBuilder(URI.create(url())).build(), HttpResponse.BodyHandlers.ofInputStream()).body();
					ZipInputStream zip = new ZipInputStream(stream)) {
				while (true) {
					ZipEntry entry = zip.getNextEntry();
					if (entry == null)
						break;
					if (!entry.isDirectory())
						writer.put(Paths.get(entry.getName()), IOUtils.toByteArray(zip));
				}
			}
		});
	}
}
