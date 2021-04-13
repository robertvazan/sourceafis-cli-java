// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import com.machinezoo.sourceafis.cli.utils.*;

public class Checksum {
	public static void report() {
		var table = new Pretty.Table("Data", "Hash");
		table.add("Templates", Pretty.hash(TemplateChecksum.global(), "templates"));
		table.add("Scores", Pretty.hash(ScoreChecksum.global(), "scores"));
		table.add("Extractor transparency", Pretty.hash(ExtractorTransparencyChecksum.global(), "transparency", "extractor"));
		table.add("Matcher transparency", Pretty.hash(MatcherTransparencyChecksum.global(), "transparency", "matcher"));
		table.add("Match transparency", Pretty.hash(MatchTransparencyChecksum.global(), "transparency", "match"));
		Pretty.print(table.format());
	}
}
