// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.args;

import java.util.*;

public abstract class Option {
	public abstract String name();
	public abstract List<String> parameters();
	public abstract String description();
	public abstract void run(List<String> parameters);
	public String fallback() {
		return null;
	}
}
