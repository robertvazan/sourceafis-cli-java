// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli;

import org.apache.commons.lang3.exception.*;
import com.machinezoo.sourceafis.cli.benchmarks.*;
import com.machinezoo.sourceafis.cli.checksums.*;
import com.machinezoo.sourceafis.cli.config.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.logs.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class Main {
	public static void main(String args[]) {
		try {
			var parser = new CommandParser()
				.add(new HomeOption())
				.add(new NormalizationOption())
				.add(new BaselineOption())
				.add(new VersionReport())
				.add(new BenchmarkOverview())
				.add(new AccuracyBenchmark())
				.add(new SpeedOverview())
				.add(new ExtractionSpeed())
				.add(new IdentificationSpeed())
				.add(new VerificationSpeed())
				.add(new DeserializationSpeed())
				.add(new FootprintBenchmark())
				.add(new Checksum())
				.add(new TemplateChecksum())
				.add(new ScoreChecksum())
				.add(new ExtractorChecksum())
				.add(new ProbeChecksum())
				.add(new ComparisonChecksum())
				.add(new ExtractorLog())
				.add(new ProbeLog())
				.add(new ComparisonLog())
				.add(new GrayscaleExport())
				.add(new Purge());
			var command = parser.parse(args);
			if (Configuration.baseline != null) {
				Configuration.baselineMode = true;
				command.run();
				Configuration.baselineMode = false;
			}
			command.run();
		} catch (Throwable ex) {
			ExceptionUtils.printRootCauseStackTrace(ex);
			System.exit(1);
		}
	}
}
