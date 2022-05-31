// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.logs;

import com.machinezoo.sourceafis.cli.inputs.*;

public interface LogOperation {
	String name();
	String description();
	void log(Dataset dataset, LogWriter writer);
}
