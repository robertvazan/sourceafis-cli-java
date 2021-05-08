// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.config;

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
	public void run() {
		Configuration.normalized = true;
	}
}
