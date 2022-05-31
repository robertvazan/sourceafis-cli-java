// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.logs;

import java.nio.file.*;

public interface LogWriter {
	void put(Path path, Runnable action);
}
