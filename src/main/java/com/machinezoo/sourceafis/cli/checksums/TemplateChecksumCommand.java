// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class TemplateChecksumCommand implements SimpleCommand {
	@Override
	public List<String> subcommand() {
		return List.of("checksum", "templates");
	}
	@Override
	public String description() {
		return "Compute consistency checksum of templates.";
	}
	@Override
	public void run() {
		var table = new PrettyTable();
		for (var profile : Profile.all()) {
			MissingBaselineException.silence().run(() -> {
				var stats = TemplateChecksumCache.sum(profile);
				table.add("Dataset", profile.name());
				table.add("Count", Pretty.length(stats.count()));
				table.add("Length", Pretty.length(stats.length() / stats.count(), profile.name(), "length"));
				table.add("Normalized", Pretty.length(stats.normalized() / stats.count(), profile.name(), "normalized"));
				table.add("Total", Pretty.length(stats.normalized(), profile.name(), "total"));
				table.add("Hash", Pretty.hash(stats.hash(), profile.name(), "hash"));
			});
		}
		table.print();
	}
}
