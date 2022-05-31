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
				.add(new VersionCommand())
				.add(new BenchmarkCommand())
				.add(new AccuracyCommand())
				.add(new SpeedOverviewCommand())
				.add(new SpeedCommand(new ExtractionSpeedCache()))
				.add(new SpeedCommand(new IdentificationSpeedCache()))
				.add(new SpeedCommand(new VerificationSpeedCache()))
				.add(new SpeedCommand(new DeserializationSpeedCache()))
				.add(new FootprintCommand())
				.add(new ChecksumCommand())
				.add(new TemplateChecksumCommand())
				.add(new ScoreChecksumCommand())
				.add(new TransparencyChecksumCommand(new ExtractionChecksum()))
				.add(new TransparencyChecksumCommand(new ProbeChecksum()))
				.add(new TransparencyChecksumCommand(new ComparisonChecksum()))
				.add(new LogCommand(new ExtractionLog()))
				.add(new LogCommand(new ProbeLog()))
				.add(new LogCommand(new ComparisonLog()))
				.add(new GrayscaleExportCommand())
				.add(new PurgeCommand());
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
