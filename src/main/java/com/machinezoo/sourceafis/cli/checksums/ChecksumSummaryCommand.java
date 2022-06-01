// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import java.util.function.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public record ChecksumSummaryCommand() implements SimpleCommand {
	@Override
	public List<String> subcommand() {
		return List.of("checksum");
	}
	@Override
	public String description() {
		return "Compute consistency checksum of all algorithm outputs.";
	}
	private record GlobalHasher(String name, Supplier<byte[]> runner) {
		GlobalHasher(String name, ChecksumOperation operation) {
			this(name, operation::global);
		}
	}
	private static byte[] total() {
		var sum = new Hasher();
		for (var hasher : GLOBAL_HASHERS)
			if (hasher != TOTAL)
				sum.add(hasher.runner.get());
		return sum.compute();
	}
	private static final GlobalHasher TOTAL = new GlobalHasher("Total", () -> total());
	private static final GlobalHasher[] GLOBAL_HASHERS = new GlobalHasher[] {
		new GlobalHasher("Templates", TemplateChecksumCache::global),
		new GlobalHasher("Scores", ScoreChecksumCache::global),
		new GlobalHasher("Extractor", new ExtractionChecksum()),
		new GlobalHasher("Probe", new ProbeChecksum()),
		new GlobalHasher("Comparison", new ComparisonChecksum()),
		TOTAL
	};
	@Override
	public void run() {
		var table = new PrettyTable();
		for (var hasher : GLOBAL_HASHERS) {
			MissingBaselineException.silence().run(() -> {
				table.add("Data", hasher.name);
				table.add("Hash", Pretty.hash(hasher.runner.get(), hasher.name));
			});
		}
		table.print();
	}
}
