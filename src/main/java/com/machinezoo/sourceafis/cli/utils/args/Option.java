// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.args;

import java.util.*;

public abstract class Option {
	public abstract String name();
	public List<String> parameters() {
		return Collections.emptyList();
	}
	public abstract String description();
	public void run() {
	}
	public void run(List<String> parameters) {
		run();
	}
	public String fallback() {
		return null;
	}
}
