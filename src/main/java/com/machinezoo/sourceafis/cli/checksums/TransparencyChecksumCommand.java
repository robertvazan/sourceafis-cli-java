// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public record TransparencyChecksumCommand(ChecksumOperation operation) implements SimpleCommand {
	@Override
	public List<String> subcommand() {
		return List.of("checksum", "transparency", operation.name());
	}
	@Override
	public String description() {
		return operation.description();
	}
	@Override
	public void run() {
		MissingBaselineException.silence().run(() -> {
			var table = new PrettyTable();
			for (var row : operation.sum(Profile.everything()).rows()) {
				var stats = row.stats();
				table.add("Key", row.key());
				table.add("MIME", stats.mime());
				table.add("Count", Pretty.length(stats.count()));
				table.add("Length", Pretty.length(stats.length() / stats.count(), row.key(), "length"));
				table.add("Normalized", Pretty.length(stats.normalized() / stats.count(), row.key(), "normalized"));
				table.add("Total", Pretty.length(stats.normalized(), row.key(), "total"));
				table.add("Hash", Pretty.hash(stats.hash(), row.key(), "hash"));
			}
			table.print();
		});
	}
}
