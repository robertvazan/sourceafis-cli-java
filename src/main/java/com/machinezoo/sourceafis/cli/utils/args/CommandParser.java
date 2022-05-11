// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.args;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.*;
import one.util.streamex.*;

public class CommandParser {
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
			Pretty.print("SourceAFIS CLI for Java");
			Pretty.print("");
			Pretty.print("Available subcommands:");
			for (var command : commands) {
				Pretty.format("\t{0}{1}", String.join(" ", command.subcommand()), StreamEx.of(command.parameters()).map(p -> " <" + p + ">").joining());
				Pretty.format("\t\t{0}", command.description());
			}
			Pretty.print("");
			Pretty.print("Available options:");
			for (var option : options) {
				Pretty.format("\t--{0}{1}", option.name(), StreamEx.of(option.parameters()).map(p -> " <" + p + ">").joining());
				Pretty.format("\t\t{0}", option.description());
				if (option.fallback() != null)
					Pretty.format("\t\tDefault: {0}", option.fallback());
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
