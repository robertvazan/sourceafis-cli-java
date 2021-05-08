// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

import java.util.*;
import org.apache.commons.io.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.cli.config.*;
import com.machinezoo.sourceafis.cli.utils.args.*;

public class Purge extends Command {
	@Override
	public List<String> subcommand() {
		return List.of("purge");
	}
	@Override
	public String description() {
		return "Remove cached data except downloads.";
	}
	@Override
	public void run() {
		Exceptions.sneak().run(() -> FileUtils.deleteDirectory(Configuration.output().toFile()));
	}
}
