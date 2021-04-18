// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.args;

import java.util.*;
import java.util.function.*;
import org.slf4j.*;
import one.util.streamex.*;

public class Command {
	private static final Logger logger = LoggerFactory.getLogger(Command.class);
	final List<String> path;
	Consumer<List<String>> action;
	List<String> parameters;
	String description;
	public Command(String... path) {
		this.path = List.of(path);
	}
	public Command action(Runnable action) {
		this.action = p -> action.run();
		parameters = Collections.emptyList();
		return this;
	}
	public Command action(String parameter, Consumer<String> action) {
		this.action = p -> action.accept(p.get(0));
		parameters = List.of(parameter);
		return this;
	}
	public void register(String description) {
		this.description = description;
		CommandRegistry.register(this);
	}
	void help() {
		logger.info("\t{}{}", String.join(" ", path), StreamEx.of(parameters).map(p -> " <" + p + ">").joining());
		logger.info("\t\t{}", description);
	}
}
