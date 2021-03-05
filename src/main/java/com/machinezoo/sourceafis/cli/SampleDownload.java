// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.concurrent.atomic.*;
import java.util.zip.*;
import org.apache.commons.io.*;
import org.slf4j.*;
import com.machinezoo.noexception.*;

class SampleDownload {
	static final String[] AVAILABLE = new String[] {
		"fvc2000-1b",
		"fvc2000-2b",
		"fvc2000-3b",
		"fvc2000-4b",
		"fvc2002-1b",
		"fvc2002-2b",
		"fvc2002-3b",
		"fvc2002-4b",
		"fvc2004-1b",
		"fvc2004-2b",
		"fvc2004-3b",
		"fvc2004-4b"
	};
	private static String url(String dataset) {
		switch (dataset) {
		case "fvc2000-1b":
			return "https://cdn.machinezoo.com/h/5JV6zPvfEdLsVByxgQfx8mZzp_GK1uB4gMbpTRVk3vI/fvc2000-1b-png.zip";
		case "fvc2000-2b":
			return "https://cdn.machinezoo.com/h/7AnG1QKQFIO_SPE2QfQOOh2niRt5SwIWKTBNMODhy9k/fvc2000-2b-png.zip";
		case "fvc2000-3b":
			return "https://cdn.machinezoo.com/h/zYWii7GxCYODyMQXL_0V5XYBAeGDL0ZyAn9ueGJxKWo/fvc2000-3b-png.zip";
		case "fvc2000-4b":
			return "https://cdn.machinezoo.com/h/iJwQy6OqL8GfKmb-8CahSPPe-TSO1Il-84ECZbzH7BU/fvc2000-4b-png.zip";
		case "fvc2002-1b":
			return "https://cdn.machinezoo.com/h/27Ywz3grZYFSPdVGhdksFPJ7LMLH5XXXzXdLoi6OmO0/fvc2002-1b-png.zip";
		case "fvc2002-2b":
			return "https://cdn.machinezoo.com/h/PN2JaZ2IsHWGcCakDBFXM4bxscockSjqTVmISrFYaes/fvc2002-2b-png.zip";
		case "fvc2002-3b":
			return "https://cdn.machinezoo.com/h/CjrVS4yjjF0EovpOMU6onMAdUbw86LF9Q9ZZAaqbx1A/fvc2002-3b-png.zip";
		case "fvc2002-4b":
			return "https://cdn.machinezoo.com/h/XMlAqAkmJBOnb-u2EpoASAi4tZv8-06J3YPFyrYD_AM/fvc2002-4b-png.zip";
		case "fvc2004-1b":
			return "https://cdn.machinezoo.com/h/cKRPCJLDup30Q7xpWNcsaKZNp7y8am1zpP3PKZSutto/fvc2004-1b-png.zip";
		case "fvc2004-2b":
			return "https://cdn.machinezoo.com/h/6XCAX2TZjUZnm2bwBjvxhx4VSOk7D_-q7AO3yXfF9n8/fvc2004-2b-png.zip";
		case "fvc2004-3b":
			return "https://cdn.machinezoo.com/h/5A_W-WTx6R268rRmJJGhknGMbJcB8ik2RUk436e9_BA/fvc2004-3b-png.zip";
		case "fvc2004-4b":
			return "https://cdn.machinezoo.com/h/pP7exv5puFbdtkUNXmUucm9TgSbj94-0dEX_Fcj-jkQ/fvc2004-4b-png.zip";
		default:
			throw new IllegalArgumentException();
		}
	}
	static Path directory(String dataset) {
		return PersistentCache.home.resolve("samples").resolve(dataset);
	}
	private static final Logger logger = LoggerFactory.getLogger(SampleDownload.class);
	private static final AtomicBoolean reported = new AtomicBoolean();
	static Path unpack(String dataset) {
		var url = url(dataset);
		var directory = directory(dataset);
		if (!Files.isDirectory(directory)) {
			Exceptions.sneak().run(() -> {
				var temporary = directory.resolveSibling("tmp");
				if (Files.exists(temporary))
					FileUtils.deleteDirectory(temporary.toFile());
				Files.createDirectories(temporary);
				if (!reported.getAndSet(true))
					logger.info("Downloading sample fingerprints...");
				try (	InputStream stream = new URL(url).openStream();
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
		return directory;
	}
}
