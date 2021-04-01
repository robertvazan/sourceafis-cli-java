// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.utils;

import java.util.*;
import java.util.function.*;
import org.slf4j.*;
import com.machinezoo.sourceafis.*;
import it.unimi.dsi.fastutil.ints.*;
import one.util.streamex.*;

public class Args {
	private static final Logger logger = LoggerFactory.getLogger(Args.class);
	private static final CommandGroup commandRoot = new CommandGroup();
	private static final List<Command> commands = new ArrayList<>();
	private static class CommandGroup {
		final Map<String, CommandGroup> subcommands = new HashMap<>();
		final Int2ObjectMap<Command> overloads = new Int2ObjectOpenHashMap<>();
		void register(int depth, Command command) {
			if (depth < command.path.size())
				subcommands.computeIfAbsent(command.path.get(depth), c -> new CommandGroup()).register(depth + 1, command);
			else
				overloads.put(command.parameters.size(), command);
		}
	}
	public static class Command {
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
			commandRoot.register(0, this);
			commands.add(this);
		}
		void help() {
			logger.info("\t{}{}", String.join(" ", path), StreamEx.of(parameters).map(p -> " <" + p + ">").joining());
			logger.info("\t\t{}", description);
		}
	}
	private static final Map<String, Option> optionMap = new HashMap<>();
	private static final List<Option> options = new ArrayList<>();
	public static class Option {
		final String name;
		List<String> parameters;
		Consumer<List<String>> action;
		String description;
		Supplier<String> fallback;
		public Option(String name) {
			this.name = name;
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
			optionMap.put(name, this);
			options.add(this);
		}
		void help() {
			logger.info("\t--{}{}", name, StreamEx.of(parameters).map(p -> " <" + p + ">").toList());
			logger.info("\t\t{}", description);
			if (fallback != null)
				logger.info("\t\tDefault: {}", fallback.get());
		}
	}
	public static void evaluate(String args[]) {
		if (args.length == 0) {
			logger.info("SourceAFIS CLI for Java {}", FingerprintCompatibility.version());
			logger.info("");
			logger.info("Available subcommands:");
			for (var command : commands)
				command.help();
			logger.info("");
			logger.info("Available options:");
			for (var option : options)
				option.help();
			return;
		}
		int consumed = 0;
		var group = commandRoot;
		var commandArgs = new ArrayList<String>();
		while (consumed < args.length) {
			var arg = args[consumed];
			++consumed;
			if (arg.startsWith("--")) {
				var name = arg.substring(2);
				var option = optionMap.get(name);
				if (option == null)
					throw new IllegalArgumentException("Unknown option: " + arg);
				var optionArgs = new ArrayList<String>();
				for (int i = 0; i < option.parameters.size(); ++i) {
					if (consumed >= args.length)
						throw new IllegalArgumentException("Missing argument <" + option.parameters.get(i) + "> for option '" + arg + "'.");
					optionArgs.add(args[consumed]);
					++consumed;
				}
				option.action.accept(optionArgs);
			} else {
				if (commandArgs.isEmpty() && group.subcommands.containsKey(arg))
					group = group.subcommands.get(arg);
				else
					commandArgs.add(arg);
			}
		}
		if (group == commandRoot && commandArgs.isEmpty())
			throw new IllegalArgumentException("Specify subcommand.");
		var command = group.overloads.get(commandArgs.size());
		if (command == null)
			throw new IllegalArgumentException("Unrecognized subcommand.");
		command.action.accept(commandArgs);
	}
}
