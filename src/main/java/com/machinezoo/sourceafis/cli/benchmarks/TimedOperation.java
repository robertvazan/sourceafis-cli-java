// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import com.machinezoo.sourceafis.cli.utils.*;

public abstract class TimedOperation<K> {
	public abstract void prepare(K id);
	public abstract void execute();
	public abstract void blackhole(Hasher hasher);
}
