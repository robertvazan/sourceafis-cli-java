// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.nio.file.*;
import com.machinezoo.sourceafis.*;

public class Configuration {
	private static Path home;
	static {
		/*
		 * First try XDG_* variables. Data directories may be in strange locations, for example inside flatpak.
		 */
		var configured = System.getenv("XDG_CACHE_HOME");
		Path root;
		if (configured != null)
			root = Paths.get(configured);
		else {
			/*
			 * Fall back to XDG_* default. This will perform poorly on Windows, but it will work.
			 */
			root = Paths.get(System.getProperty("user.home"), ".cache");
		}
		home = root.resolve("sourceafis");
	}
	public static Path home() {
		return home;
	}
	public static void configureHome(String path) {
		home = Paths.get(path).toAbsolutePath();
	}
	public static boolean normalized;
	public static Path baseline;
	public static boolean baselineMode;
	public static Path output() {
		if (baselineMode)
			return home().resolve(baseline);
		else
			return home().resolve("java").resolve(FingerprintCompatibility.version());
	}
}
