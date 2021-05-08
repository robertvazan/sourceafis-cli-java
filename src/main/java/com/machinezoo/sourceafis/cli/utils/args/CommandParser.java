// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.args;

import java.util.*;
import org.slf4j.*;
import one.util.streamex.*;

public class CommandParser {
	private static final Logger logger = LoggerFactory.getLogger(CommandParser.class);
	private final CommandGroup commandRoot = new CommandGroup();
	private final List<Command> commands = new ArrayList<>();
	private final Map<String, Option> optionMap = new HashMap<>();
	private final List<Option> options = new ArrayList<>();
	public CommandParser add(Command command) {
		commandRoot.add(0, command);
		commands.add(command);
		return this;
	}
	public CommandParser add(Option option) {
		optionMap.put(option.name(), option);
		options.add(option);
		return this;
	}
	public Runnable parse(String[] args) {
		if (args.length == 0) {
			logger.info("SourceAFIS CLI for Java");
			logger.info("");
			logger.info("Available subcommands:");
			for (var command : commands) {
				logger.info("\t{}{}", String.join(" ", command.subcommand()), StreamEx.of(command.parameters()).map(p -> " <" + p + ">").joining());
				logger.info("\t\t{}", command.description());
			}
			logger.info("");
			logger.info("Available options:");
			for (var option : options) {
				logger.info("\t--{}{}", option.name(), StreamEx.of(option.parameters()).map(p -> " <" + p + ">").joining());
				logger.info("\t\t{}", option.description());
				if (option.fallback() != null)
					logger.info("\t\tDefault: {}", option.fallback());
			}
			System.exit(0);
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
				for (int i = 0; i < option.parameters().size(); ++i) {
					if (consumed >= args.length)
						throw new IllegalArgumentException("Missing argument <" + option.parameters().get(i) + "> for option '" + arg + "'.");
					optionArgs.add(args[consumed]);
					++consumed;
				}
				option.run(optionArgs);
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
		return () -> command.run(commandArgs);
	}
}
