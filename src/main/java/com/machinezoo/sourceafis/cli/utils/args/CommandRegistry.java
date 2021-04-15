// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.utils.args;

import java.util.*;
import org.slf4j.*;
import com.machinezoo.sourceafis.*;

public class CommandRegistry {
	private static final Logger logger = LoggerFactory.getLogger(CommandRegistry.class);
	private static final CommandGroup commandRoot = new CommandGroup();
	private static final List<Command> commands = new ArrayList<>();
	private static final Map<String, Option> optionMap = new HashMap<>();
	private static final List<Option> options = new ArrayList<>();
	public static void register(Command command) {
		commandRoot.register(0, command);
		commands.add(command);
	}
	public static void register(Option option) {
		optionMap.put(option.name, option);
		options.add(option);
	}
	public static Runnable parse(String args[]) {
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
		return () -> command.action.accept(commandArgs);
	}
}
