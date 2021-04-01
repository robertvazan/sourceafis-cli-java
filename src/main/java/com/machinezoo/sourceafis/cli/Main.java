// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import org.apache.commons.lang3.exception.*;
import org.slf4j.*;
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
	}
	private static void registerCommands() {
		// version - Show SourceAFIS version.
		// extract <image-path> <template-path>
		// extract <width> <height> <image-path> <template-path>
		// match <probe-path> <candidate-path>
		// zip extractor <image-path> <zip-path>
		// zip matcher <probe-path> <candidate-path> <zip-path>
		new Args.Command("accuracy")
			.action(Accuracy::report)
			.register("Measure algorithm accuracy.");
		// benchmark - speed benchmarks
		new Args.Command("footprint")
			.action(Footprint::report)
			.register("Measure template footprint.");
		new Args.Command("log", "extractor")
			.action("key", LogExtractor::collect)
			.register("Log extractor transparency data for given key.");
		new Args.Command("log", "extractor", "normalized")
			.action("key", LogExtractorNormalized::collect)
			.register("Log normalized extractor transparency data for given key.");
		// log matcher <key>
		// log matcher normalized <key>
		// checksum
		new Args.Command("checksum", "templates")
			.action(ChecksumTemplates::report)
			.register("Compute consistency checksum of templates.");
		// checksum scores
		new Args.Command("checksum", "transparency", "extractor")
			.action(ChecksumTransparencyExtractor::report)
			.register("Compute consistency checksum of extractor transparency data.");
		// checksum transparency matcher
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
			Args.evaluate(args);
		} catch (Throwable ex) {
			logger.error("{}", StreamEx.of(ExceptionUtils.getThrowableList(ex)).map(x -> ExceptionUtils.getMessage(x)).joining(" -> "));
			System.exit(1);
		}
	}
}
