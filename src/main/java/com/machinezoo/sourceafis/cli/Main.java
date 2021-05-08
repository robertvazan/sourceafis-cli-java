// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli;

import java.nio.file.*;
import java.util.*;
import org.apache.commons.lang3.exception.*;
import org.slf4j.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.benchmarks.*;
import com.machinezoo.sourceafis.cli.checksums.*;
import com.machinezoo.sourceafis.cli.logs.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;
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
	private static class VersionReport extends Command {
		@Override
		public List<String> subcommand() {
			return List.of("version");
		}
		@Override
		public String description() {
			return "Report version of SourceAFIS library being used.";
		}
		@Override
		public void run() {
			Pretty.print(FingerprintCompatibility.version());
		}
	}
	private static void registerCommands() {
		CommandRegistry.register(new VersionReport());
		CommandRegistry.register(new BenchmarkOverview());
		CommandRegistry.register(new AccuracyBenchmark());
		CommandRegistry.register(new SpeedOverview());
		CommandRegistry.register(new ExtractionSpeed());
		CommandRegistry.register(new IdentificationSpeed());
		CommandRegistry.register(new VerificationSpeed());
		CommandRegistry.register(new ProbeSpeed());
		CommandRegistry.register(new SerializationSpeed());
		CommandRegistry.register(new DeserializationSpeed());
		CommandRegistry.register(new FootprintBenchmark());
		CommandRegistry.register(new Checksum());
		CommandRegistry.register(new TemplateChecksum());
		CommandRegistry.register(new ScoreChecksum());
		CommandRegistry.register(new ExtractorChecksum());
		CommandRegistry.register(new MatcherChecksum());
		CommandRegistry.register(new MatchChecksum());
		CommandRegistry.register(new ExtractorLog());
		CommandRegistry.register(new MatcherLog());
		CommandRegistry.register(new MatchLog());
		CommandRegistry.register(new GrayscaleExport());
		CommandRegistry.register(new Purge());
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
