// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.args;

import java.util.*;
import java.util.function.*;
import org.slf4j.*;
import one.util.streamex.*;

public class Option {
	private static final Logger logger = LoggerFactory.getLogger(Option.class);
	final String name;
	List<String> parameters;
	Consumer<List<String>> action;
	String description;
	Supplier<String> fallback;
	public Option(String name) {
		this.name = name;
	}
	public Option action(Runnable action) {
		this.action = p -> action.run();
		parameters = Collections.emptyList();
		return this;
	}
	public Option action(String parameter, Consumer<String> action) {
		this.action = p -> action.accept(p.get(0));
		parameters = List.of(parameter);
		return this;
	}
	public Option fallback(Supplier<String> fallback) {
		this.fallback = fallback;
		return this;
	}
	public Option fallback(String fallback) {
		return fallback(() -> fallback);
	}
	public void register(String description) {
		this.description = description;
		CommandRegistry.register(this);
	}
	void help() {
		logger.info("\t--{}{}", name, StreamEx.of(parameters).map(p -> " <" + p + ">").joining());
		logger.info("\t\t{}", description);
		if (fallback != null)
			logger.info("\t\tDefault: {}", fallback.get());
	}
}
