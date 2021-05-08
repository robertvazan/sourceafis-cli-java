// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.args;

import java.util.*;
import it.unimi.dsi.fastutil.ints.*;

public class CommandGroup {
	final Map<String, CommandGroup> subcommands = new HashMap<>();
	final Int2ObjectMap<Command> overloads = new Int2ObjectOpenHashMap<>();
	void register(int depth, Command command) {
		if (depth < command.subcommand().size())
			subcommands.computeIfAbsent(command.subcommand().get(depth), c -> new CommandGroup()).register(depth + 1, command);
		else
			overloads.put(command.parameters().size(), command);
	}
}
