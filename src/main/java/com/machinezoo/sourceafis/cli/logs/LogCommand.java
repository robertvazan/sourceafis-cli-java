// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.logs;

import java.util.*;
import com.machinezoo.sourceafis.cli.config.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import one.util.streamex.*;

public record LogCommand(LogOperation operation) implements Command {
	@Override
	public List<String> subcommand() {
		return List.of("log", operation.name());
	}
	@Override
	public String description() {
		return operation.description();
	}
	@Override
	public List<String> parameters() {
		return List.of("dataset", "key");
	}
	@Override
	public void run(List<String> parameters) {
		var dataset = StreamEx.of(Dataset.values())
			.filter(ds -> ds.codename().equals(parameters.get(0)))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Unknown dataset."));
		var key = parameters.get(1);
		var cache = new LogCache(operation, dataset, key, Configuration.normalized);
		Pretty.format("Logs saved: {0}", cache.load().directory());
	}
}
