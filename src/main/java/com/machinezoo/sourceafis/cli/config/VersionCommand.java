// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.config;

import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;

public record VersionCommand() implements SimpleCommand {
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
