// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

interface CacheCompression {
	String extension();
	byte[] compress(byte[] data);
	byte[] decompress(byte[] compressed);
	static CacheCompression select(String extension) {
		if (extension.equals(".cbor"))
			return new GzipCompression();
		return new TrivialCompression();
	}
}
