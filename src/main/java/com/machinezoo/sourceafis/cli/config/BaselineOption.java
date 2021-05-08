// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.config;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.sourceafis.cli.utils.args.*;

public class BaselineOption extends Option {
	@Override
	public String name() {
		return "baseline";
	}
	@Override
	public List<String> parameters() {
		return List.of("path");
	}
	@Override
	public String description() {
		return "Compare with output of another SourceAFIS CLI. Path may be relative to cache directory, e.g. 'java/1.2.3'.";
	}
	@Override
	public void run(List<String> parameters) {
		Configuration.baseline = Paths.get(parameters.get(0));
	}
}
