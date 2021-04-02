// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.nio.file.*;
import org.apache.commons.lang3.exception.*;
import org.slf4j.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.outputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	private static void registerOptions() {
		new Args.Option("home")
			.action("path", Configuration::configureHome)
			.fallback(Configuration.home().toString())
			.register("Location of cache directory.");
		new Args.Option("normalize")
			.action(() -> Configuration.normalized = true)
			.register("Log normalized transparency data instead of raw data obtained from the library.");
		new Args.Option("baseline")
			.action("path", p -> Configuration.baseline = Paths.get(p))
			.register("Compare with output of another SourceAFIS CLI. Path may be relative to cache directory, e.g. 'java/1.2.3'.");
	}
	private static void registerCommands() {
		new Args.Command("version")
			.action(() -> Pretty.print("SourceAFIS for Java " + FingerprintCompatibility.version()))
			.register("Report version of SourceAFIS library being used.");
		// extract <image-path> <template-path>
		// extract <width> <height> <image-path> <template-path>
		// compare <probe-path> <candidate-path>
		// log extractor <image-path> <zip-path>
		// log matcher <probe-path> <candidate-path> <zip-path>
		// benchmark - accuracy + speed + footprint
		new Args.Command("benchmark", "accuracy")
			.action(BenchmarkAccuracy::report)
			.register("Measure algorithm accuracy.");
		// benchmark speed
		new Args.Command("benchmark", "footprint")
			.action(BenchmarkFootprint::report)
			.register("Measure template footprint.");
		new Args.Command("log", "extractor")
			.action("key", LogExtractor::collect)
			.register("Log extractor transparency data for given key.");
		new Args.Command("log", "matcher")
			.action("key", LogMatcher::collect)
			.register("Log matcher transparency data for given key.");
		// checksum
		new Args.Command("checksum", "templates")
			.action(ChecksumTemplates::report)
			.register("Compute consistency checksum of templates.");
		// checksum scores
		new Args.Command("checksum", "transparency", "extractor")
			.action(ChecksumTransparencyExtractor::report)
			.register("Compute consistency checksum of extractor transparency data.");
		new Args.Command("checksum", "transparency", "matcher")
			.action(ChecksumTransparencyMatcher::report)
			.register("Compute consistency checksum of matcher transparency data.");
		new Args.Command("export", "png")
			.action(ExportPng::export)
			.register("Convert sample images to PNG.");
		new Args.Command("export", "grayscale")
			.action(ExportGrayscale::export)
			.register("Convert sample images to grayscale.");
		// purge - remove cached data except downloads
	}
	public static void main(String args[]) {
		try {
			registerOptions();
			registerCommands();
			var command = Args.parse(args);
			if (Configuration.baseline != null) {
				Configuration.baselineMode = true;
				command.run();
				Configuration.baselineMode = false;
			}
			command.run();
		} catch (Throwable ex) {
			logger.error("{}", StreamEx.of(ExceptionUtils.getThrowableList(ex)).map(x -> ExceptionUtils.getMessage(x)).joining(" -> "));
			System.exit(1);
		}
	}
}
