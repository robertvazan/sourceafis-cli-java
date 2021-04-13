// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import com.machinezoo.sourceafis.cli.utils.*;

public class Checksum {
	public static void report() {
		var table = new Pretty.Table("Data", "Hash");
		table.add("Templates", Pretty.hash(ChecksumTemplates.global()));
		table.add("Scores", Pretty.hash(ChecksumScores.global()));
		table.add("Extractor transparency", Pretty.hash(ChecksumTransparencyExtractor.global()));
		table.add("Matcher transparency", Pretty.hash(ChecksumTransparencyMatcher.global()));
		table.add("Match transparency", Pretty.hash(ChecksumTransparencyMatch.global()));
		Pretty.print(table.format());
	}
}
