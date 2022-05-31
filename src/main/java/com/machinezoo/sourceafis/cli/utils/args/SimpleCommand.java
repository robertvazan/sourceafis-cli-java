// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.args;

import java.util.*;

public interface SimpleCommand extends Command {
	void run();
	@Override
	default List<String> parameters() {
		return Collections.emptyList();
	}
	@Override
	default void run(List<String> parameters) {
		run();
	}
}
