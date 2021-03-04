//Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.zip.*;
import org.apache.commons.io.*;
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
			return "https://cdn.machinezoo.com/h/O_mBtWH-PXJ4ETJJe_G-Z9EmJoJLfq4srVw23tTEMZw/fvc2000-1b.zip";
		case "fvc2000-2b":
			return "https://cdn.machinezoo.com/h/zJB3za1cEccZjZmkV6KfD5Jk_ffegOmOcTZmG4PpaSM/fvc2000-2b.zip";
		case "fvc2000-3b":
			return "https://cdn.machinezoo.com/h/oGd8JtGpIzDSprQSsGNpbJuAAjNLTZxc_1Rol6t5deA/fvc2000-3b.zip";
		case "fvc2000-4b":
			return "https://cdn.machinezoo.com/h/624mM3sTCV8kZy75UilOMkEl-RFjv_9lGXIr9I7dzH8/fvc2000-4b.zip";
		case "fvc2002-1b":
			return "https://cdn.machinezoo.com/h/ZGusAOeUs8zVmtCtFdUbNCyAqV2qFEtaFw2GWxyrRFo/fvc2002-1b.zip";
		case "fvc2002-2b":
			return "https://cdn.machinezoo.com/h/N3FvC0y0dt684GsQkSrKynyj6PUYswCV7ak2xjPZFGI/fvc2002-2b.zip";
		case "fvc2002-3b":
			return "https://cdn.machinezoo.com/h/46O3Whe353EeJn8aIPCo0zWnddd5fSXsvVXSKTQCrOA/fvc2002-3b.zip";
		case "fvc2002-4b":
			return "https://cdn.machinezoo.com/h/GSLM0-GZULWBL2Dc6Lk6QuTs_FcwZgGHi6NiJrZupNc/fvc2002-4b.zip";
		case "fvc2004-1b":
			return "https://cdn.machinezoo.com/h/Owa1eWSvirTpEQ4NQfdJzKxNsBPfwJftpJjLkaVnoiw/fvc2004-1b.zip";
		case "fvc2004-2b":
			return "https://cdn.machinezoo.com/h/S7yLI6vOiFvog-PniaOCSdQ4etoNxGAEH81MfHvl_C8/fvc2004-2b.zip";
		case "fvc2004-3b":
			return "https://cdn.machinezoo.com/h/0zZbQizCzt2eVPE-QdKEz3VaDiKERGc1aFFGPouAirE/fvc2004-3b.zip";
		case "fvc2004-4b":
			return "https://cdn.machinezoo.com/h/nAFmSXlgm-bbTflylBBn5dRe775haHKgmK1T5tVnHRw/fvc2004-4b.zip";
		default:
			throw new IllegalArgumentException();
		}
	}
	static Path directory(String dataset) {
		return PersistentCache.home.resolve("samples").resolve(dataset);
	}
	static Path unpack(String dataset) {
		var url = url(dataset);
		var directory = directory(dataset);
		if (!Files.isDirectory(directory)) {
			Exceptions.sneak().run(() -> {
				var temporary = directory.resolveSibling("tmp");
				if (Files.exists(temporary))
					FileUtils.deleteDirectory(temporary.toFile());
				Files.createDirectories(temporary);
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
