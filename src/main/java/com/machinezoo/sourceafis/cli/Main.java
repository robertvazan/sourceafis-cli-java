// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.util.*;
import java.util.function.*;
import org.apache.commons.lang3.exception.*;
import org.slf4j.*;
import it.unimi.dsi.fastutil.ints.*;
import one.util.streamex.*;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
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
	private static class Command {
		final List<String> path;
		Consumer<List<String>> action;
		List<String> parameters;
		String description;
		Command(String... path) {
			this.path = List.of(path);
		}
		Command action(Runnable action) {
			this.action = p -> action.run();
			parameters = Collections.emptyList();
			return this;
		}
		Command action(String parameter, Consumer<String> action) {
			this.action = p -> action.accept(p.get(0));
			parameters = List.of(parameter);
			return this;
		}
		void register(String description) {
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
	private static class Option {
		final String name;
		List<String> parameters;
		Consumer<List<String>> action;
		String description;
		Supplier<String> fallback;
		Option(String name) {
			this.name = name;
		}
		Option action(String parameter, Consumer<String> action) {
			this.action = p -> action.accept(p.get(0));
			parameters = List.of(parameter);
			return this;
		}
		Option fallback(Supplier<String> fallback) {
			this.fallback = fallback;
			return this;
		}
		Option fallback(String fallback) {
			return fallback(() -> fallback);
		}
		void register(String description) {
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
	private static void registerOptions() {
		new Option("home")
			.action("path", HomeDirectory::overrideHome)
			.fallback(HomeDirectory.home.toString())
			.register("Location of cache and output directory.");
	}
	private static void registerCommands() {
		// version - Show SourceAFIS version.
		// extract <image-path> <template-path>
		// extract <width> <height> <image-path> <template-path>
		// match <probe-path> <candidate-path>
		// zip extractor <image-path> <zip-path>
		// zip matcher <probe-path> <candidate-path> <zip-path>
		// TODO: separate footprint from checksum
		new Command("accuracy")
			.action(ScalarAccuracy::report)
			.register("Measure algorithm accuracy.");
		// benchmark - speed benchmarks
		new Command("footprint")
			.action(TemplateFootprint::report)
			.register("Measure template footprint.");
		new Command("log", "extractor")
			.action("key", key -> TransparencyFile.extractor(key))
			.register("Log extractor transparency data for given key.");
		new Command("log", "extractor", "normalized")
			.action("key", key -> TransparencyFile.extractorNormalized(key))
			.register("Log normalized extractor transparency data for given key.");
		// log matcher <key>
		// log matcher normalized <key>
		// checksum
		// TODO: separate footprint from checksum
		new Command("checksum", "templates")
			.action(TemplateFootprint::report)
			.register("Compute consistency checksum of templates.");
		// checksum scores
		new Command("checksum", "transparency", "extractor")
			.action(() -> TransparencyStats.report(TransparencyStats.extractorTable()))
			.register("Compute consistency checksum of extractor transparency data.");
		// checksum transparency matcher
		new Command("generate", "png")
			.action(ImageConversion::png)
			.register("Convert sample images to PNG.");
		new Command("generate", "grayscale")
			.action(ImageConversion::gray)
			.register("Convert sample images to grayscale.");
		// purge - remove cached data except downloads
	}
	public static void main(String args[]) {
		try {
			registerOptions();
			registerCommands();
			if (args.length == 0) {
				logger.info("Available subcommands:");
				for (var command : commands)
					command.help();
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
		} catch (Throwable ex) {
			logger.error("{}", StreamEx.of(ExceptionUtils.getThrowableList(ex)).map(x -> ExceptionUtils.getMessage(x)).joining(" -> "));
			System.exit(1);
		}
	}
}
