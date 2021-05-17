// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;

public class Checksum extends Command {
	@Override
	public List<String> subcommand() {
		return List.of("checksum");
	}
	@Override
	public String description() {
		return "Compute consistency checksum of all algorithm outputs.";
	}
	@Override
	public void run() {
		var table = new PrettyTable("Data", "Hash");
		table.add("Templates", Pretty.hash(new TemplateChecksum().global(), "templates"));
		table.add("Scores", Pretty.hash(new ScoreChecksum().global(), "scores"));
		for (var transparency : List.of(new ExtractorChecksum(), new ProbeChecksum(), new MatchChecksum()))
			table.add("Transparency/" + transparency.name(), Pretty.hash(transparency.global(), "transparency", transparency.name()));
		Pretty.print(table.format());
	}
}
