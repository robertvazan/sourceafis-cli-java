// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.inputs;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;

public record GrayscaleExportCommand() implements SimpleCommand {
	@Override
	public List<String> subcommand() {
		return List.of("export", "grayscale");
	}
	@Override
	public String description() {
		return "Convert sample images to grayscale.";
	}
	@Override
	public void run() {
		for (var dataset : Dataset.all())
			new GrayscaleExportCache(dataset).load();
		Pretty.print("Done.");
	}
}
