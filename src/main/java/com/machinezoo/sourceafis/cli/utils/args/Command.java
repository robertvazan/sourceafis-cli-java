// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.args;

import java.util.*;

public interface Command {
	List<String> subcommand();
	List<String> parameters();
	String description();
	void run(List<String> parameters);
}
