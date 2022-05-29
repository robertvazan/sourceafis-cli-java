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
import com.machinezoo.sourceafis.cli.utils.*;

public interface Download {
	Path group();
	Path identity();
	String url();
	static final HttpClient client = HttpClient.newBuilder()
		.followRedirects(Redirect.NORMAL)
		.build();
	static final Once<Path> reported = new Once<>();
	default Path unpack() {
		var directory = Configuration.home.resolve("inputs").resolve(group()).resolve(identity() != null ? identity() : Paths.get(""));
		synchronized (Download.class) {
			if (!Files.isDirectory(directory)) {
				Exceptions.sneak().run(() -> {
					var temporary = directory.resolveSibling("tmp");
					if (Files.exists(temporary))
						FileUtils.deleteDirectory(temporary.toFile());
					Files.createDirectories(temporary);
					if (reported.first(group()))
						Pretty.format("Downloading {0}...", group());
					try (	InputStream stream = client.send(HttpRequest.newBuilder(URI.create(url())).build(), HttpResponse.BodyHandlers.ofInputStream()).body();
							ZipInputStream zip = new ZipInputStream(stream)) {
						while (true) {
							ZipEntry entry = zip.getNextEntry();
							if (entry == null)
								break;
							if (!entry.isDirectory())
								Files.write(temporary.resolve(entry.getName()), IOUtils.toByteArray(zip));
						}
					}
					Files.move(temporary, directory);
				});
			}
		}
		return directory;
	}
}
