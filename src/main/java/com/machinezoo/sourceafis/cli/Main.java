// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.nio.file.*;
import org.apache.commons.io.*;
import org.apache.commons.lang3.exception.*;
import org.slf4j.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.benchmarks.*;
import com.machinezoo.sourceafis.cli.checksums.*;
import com.machinezoo.sourceafis.cli.exports.*;
import com.machinezoo.sourceafis.cli.transparency.*;
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
		// benchmark - accuracy + speed + footprint
		new Args.Command("benchmark", "accuracy")
			.action(new AccuracyBenchmark())
			.register("Measure algorithm accuracy.");
		// benchmark speed
		new Args.Command("benchmark", "footprint")
			.action(new FootprintBenchmark())
			.register("Measure template footprint.");
		new Args.Command("checksum")
			.action(new Checksum())
			.register("Compute consistency checksum of all algorithm outputs.");
		new Args.Command("checksum", "templates")
			.action(new TemplateChecksum())
			.register("Compute consistency checksum of templates.");
		new Args.Command("checksum", "scores")
			.action(new ScoreChecksum())
			.register("Compute consistency checksum of similarity scores.");
		new Args.Command("checksum", "transparency", "extractor")
			.action(new ExtractorChecksum())
			.register("Compute consistency checksum of extractor transparency data.");
		new Args.Command("checksum", "transparency", "matcher")
			.action(new MatcherChecksum())
			.register("Compute consistency checksum of transparency data generated when preparing probe for matching.");
		new Args.Command("checksum", "transparency", "match")
			.action(new MatchChecksum())
			.register("Compute consistency checksum of transparency data generated during comparison of probe to candidate.");
		new Args.Command("log", "extractor")
			.action("key", new ExtractorLog()::log)
			.register("Log extractor transparency data for given key.");
		new Args.Command("log", "matcher")
			.action("key", new MatcherLog()::log)
			.register("Log transparency data for given key while preparing probe for matching.");
		new Args.Command("log", "match")
			.action("key", new MatchLog()::log)
			.register("Log transparency data for given key during comparison of probe to candidate.");
		new Args.Command("export", "png")
			.action(new PngExport())
			.register("Convert sample images to PNG.");
		new Args.Command("export", "grayscale")
			.action(new GrayscaleExport())
			.register("Convert sample images to grayscale.");
		new Args.Command("purge")
			.action(Exceptions.sneak().runnable(() -> FileUtils.deleteDirectory(Configuration.output().toFile())))
			.register("Remove cached data except downloads.");
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
