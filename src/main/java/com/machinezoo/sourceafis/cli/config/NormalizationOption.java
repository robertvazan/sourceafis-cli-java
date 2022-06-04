// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.config;

import java.util.*;
import com.machinezoo.sourceafis.cli.utils.args.*;

public class NormalizationOption extends Option {
	@Override
	public String name() {
		return "normalize";
	}
	@Override
	public String description() {
		return "Log normalized transparency data instead of raw data obtained from the library.";
	}
	@Override
	public List<String> parameters() {
		return Collections.emptyList();
	}
	@Override
	public void run(List<String> parameters) {
		Configuration.normalized = true;
	}
}
