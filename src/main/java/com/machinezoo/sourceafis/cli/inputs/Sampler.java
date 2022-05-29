// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.inputs;

public interface Sampler<K> {
	K next();
	Dataset dataset(K id);
}
