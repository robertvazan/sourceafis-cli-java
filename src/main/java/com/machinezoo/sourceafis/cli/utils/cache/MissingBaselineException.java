// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;
import com.machinezoo.noexception.*;

public class MissingBaselineException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public MissingBaselineException(Path cacheId) {
		super("Baseline data was not found: " + cacheId);
	}
	public static ExceptionHandler silence() {
		return new ExceptionHandler() {
			@Override
			public boolean handle(Throwable exception) {
				return exception instanceof MissingBaselineException;
			}
		};
	}
}
