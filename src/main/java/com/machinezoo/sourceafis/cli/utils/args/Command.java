// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.args;

import java.util.*;

public abstract class Command {
	public abstract List<String> subcommand();
	public List<String> parameters() {
		return Collections.emptyList();
	}
	public abstract String description();
	public void run() {
	}
	public void run(List<String> parameters) {
		run();
	}
}
