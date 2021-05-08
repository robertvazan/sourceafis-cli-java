// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.samples;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.concurrent.atomic.*;
import java.util.zip.*;
import org.apache.commons.io.*;
import org.slf4j.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.cli.config.*;

public class Download {
	public static final String[] DATASETS = new String[] {
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
	private static String url(String dataset, ImageFormat format) {
		switch (format) {
		case ORIGINAL:
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
		case GRAY:
			switch (dataset) {
			case "fvc2000-1b":
				return "https://cdn.machinezoo.com/h/AkBMOzR_T_0_UmxZXaubrYmwmcR1yOnByJvl3AWieMI/fvc2000-1b-gray.zip";
			case "fvc2000-2b":
				return "https://cdn.machinezoo.com/h/GBo_uNlW3166tHV-_QXTCWWo6YywNycOz_n4AUQhO3Y/fvc2000-2b-gray.zip";
			case "fvc2000-3b":
				return "https://cdn.machinezoo.com/h/6BXcjr6ZvCr4MrAYC5yiFioYCrepCiBfg68SrR0puxo/fvc2000-3b-gray.zip";
			case "fvc2000-4b":
				return "https://cdn.machinezoo.com/h/8lbaA4LGUeNFxbLbazAG-ji76_pQV3nJpCnlY__ncAc/fvc2000-4b-gray.zip";
			case "fvc2002-1b":
				return "https://cdn.machinezoo.com/h/kTJNA8M9KRnrsUPYiz4Pty5V1FPzFbdnemNqRRRsu90/fvc2002-1b-gray.zip";
			case "fvc2002-2b":
				return "https://cdn.machinezoo.com/h/7ghKDoqMr2C-OFwuqRWy-1rmdYNM3f-Zu-dy4g8SN6c/fvc2002-2b-gray.zip";
			case "fvc2002-3b":
				return "https://cdn.machinezoo.com/h/JTyQDvcQFE-WTeOKk8QuPAalDWvVV6SgVXIH1gNKQ8s/fvc2002-3b-gray.zip";
			case "fvc2002-4b":
				return "https://cdn.machinezoo.com/h/TsMV_b91QIx-cgq-FfPRH7MdE8XYJzL6ovCNJyAgYoU/fvc2002-4b-gray.zip";
			case "fvc2004-1b":
				return "https://cdn.machinezoo.com/h/3z2urqUag2AQT7m0cLmT14ofkpd6TCGlGdfagbiSScU/fvc2004-1b-gray.zip";
			case "fvc2004-2b":
				return "https://cdn.machinezoo.com/h/pTR8G8tQgaYRQSz3Gip8_eDLlg4G3OgvGfqDuoNOHkQ/fvc2004-2b-gray.zip";
			case "fvc2004-3b":
				return "https://cdn.machinezoo.com/h/I_jWMHnQE2J7qi3YOJbh9FwU0ObiFYOdunHYKqJW8K0/fvc2004-3b-gray.zip";
			case "fvc2004-4b":
				return "https://cdn.machinezoo.com/h/elY4DqdhFK8kukU9ZHmV_H8JgL2xETg1Oz74Bg1On4s/fvc2004-4b-gray.zip";
			default:
				throw new IllegalArgumentException();
			}
		default:
			throw new IllegalArgumentException();
		}
	}
	public static Path directory(String dataset, ImageFormat format) {
		String name;
		switch (format) {
		case ORIGINAL:
			name = "original";
			break;
		case GRAY:
			name = "grayscale";
			break;
		default:
			throw new IllegalArgumentException();
		}
		return Configuration.home().resolve("samples").resolve(name).resolve(dataset);
	}
	private static final Logger logger = LoggerFactory.getLogger(Download.class);
	private static final AtomicBoolean reported = new AtomicBoolean();
	public static Path unpack(String dataset, ImageFormat format) {
		var directory = directory(dataset, format);
		if (!Files.isDirectory(directory)) {
			var url = url(dataset, format);
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
