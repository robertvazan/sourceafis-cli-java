// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli;

import java.nio.file.*;
import org.apache.commons.io.*;
import org.apache.commons.lang3.exception.*;
import org.slf4j.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.benchmarks.*;
import com.machinezoo.sourceafis.cli.checksums.*;
import com.machinezoo.sourceafis.cli.logs.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import one.util.streamex.*;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	private static void registerOptions() {
		new Option("home")
			.action("path", Configuration::configureHome)
			.fallback(Configuration.home().toString())
			.register("Location of cache directory.");
		new Option("normalize")
			.action(() -> Configuration.normalized = true)
			.register("Log normalized transparency data instead of raw data obtained from the library.");
		new Option("baseline")
			.action("path", p -> Configuration.baseline = Paths.get(p))
			.register("Compare with output of another SourceAFIS CLI. Path may be relative to cache directory, e.g. 'java/1.2.3'.");
	}
	private static void registerCommands() {
		new Command("version")
			.action(() -> Pretty.print(FingerprintCompatibility.version()))
			.register("Report version of SourceAFIS library being used.");
		new Command("benchmark")
			.action(new BenchmarkOverview())
			.register("Measure algorithm accuracy, template footprint, and implementation speed.");
		new Command("benchmark", "accuracy")
			.action(new AccuracyBenchmark())
			.register("Measure algorithm accuracy.");
		new Command("benchmark", "speed")
			.action(new SpeedOverview())
			.register("Measure algorithm speed.");
		new Command("benchmark", "speed", "extraction")
			.action(new ExtractionSpeed())
			.register("Measure speed of feature extraction, i.e. FingerprintTemplate constructor.");
		new Command("benchmark", "speed", "identification")
			.action(new IdentificationSpeed())
			.register("Measure speed of identification, i.e. calling match() with non-matching candidate.");
		new Command("benchmark", "speed", "verification")
			.action(new VerificationSpeed())
			.register("Measure speed of verification, i.e. calling match() with matching candidate.");
		new Command("benchmark", "speed", "probe")
			.action(new ProbeSpeed())
			.register("Measure speed of preparing probe template for matching, i.e. FingerprintMatcher constructor.");
		new Command("benchmark", "speed", "serialization")
			.action(new SerializationSpeed())
			.register("Measure speed of template serialization.");
		new Command("benchmark", "speed", "deserialization")
			.action(new DeserializationSpeed())
			.register("Measure speed of template deserialization.");
		new Command("benchmark", "footprint")
			.action(new FootprintBenchmark())
			.register("Measure template footprint.");
		new Command("checksum")
			.action(new Checksum())
			.register("Compute consistency checksum of all algorithm outputs.");
		new Command("checksum", "templates")
			.action(new TemplateChecksum())
			.register("Compute consistency checksum of templates.");
		new Command("checksum", "scores")
			.action(new ScoreChecksum())
			.register("Compute consistency checksum of similarity scores.");
		new Command("checksum", "transparency", "extractor")
			.action(new ExtractorChecksum())
			.register("Compute consistency checksum of extractor transparency data.");
		new Command("checksum", "transparency", "matcher")
			.action(new MatcherChecksum())
			.register("Compute consistency checksum of transparency data generated when preparing probe for matching.");
		new Command("checksum", "transparency", "match")
			.action(new MatchChecksum())
			.register("Compute consistency checksum of transparency data generated during comparison of probe to candidate.");
		new Command("log", "extractor")
			.action("key", new ExtractorLog()::log)
			.register("Log extractor transparency data for given key.");
		new Command("log", "matcher")
			.action("key", new MatcherLog()::log)
			.register("Log transparency data for given key while preparing probe for matching.");
		new Command("log", "match")
			.action("key", new MatchLog()::log)
			.register("Log transparency data for given key during comparison of probe to candidate.");
		new Command("export", "grayscale")
			.action(new GrayscaleExport())
			.register("Convert sample images to grayscale.");
		new Command("purge")
			.action(Exceptions.sneak().runnable(() -> FileUtils.deleteDirectory(Configuration.output().toFile())))
			.register("Remove cached data except downloads.");
	}
	public static void main(String args[]) {
		try {
			registerOptions();
			registerCommands();
			var command = CommandRegistry.parse(args);
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
