// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.config;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.sourceafis.cli.utils.args.*;

public class HomeOption extends Option {
	@Override
	public String name() {
		return "home";
	}
	@Override
	public List<String> parameters() {
		return List.of("path");
	}
	@Override
	public String description() {
		return "Location of cache directory.";
	}
	@Override
	public String fallback() {
		return Configuration.home.toString();
	}
	@Override
	public void run(List<String> parameters) {
		Configuration.home = Paths.get(parameters.get(0)).toAbsolutePath();
	}
}
