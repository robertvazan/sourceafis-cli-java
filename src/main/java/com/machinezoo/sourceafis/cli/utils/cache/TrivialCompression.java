// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

class TrivialCompression implements CacheCompression {
	@Override
	public String extension() {
		return "";
	}
	@Override
	public byte[] compress(byte[] data) {
		return data;
	}
	@Override
	public byte[] decompress(byte[] compressed) {
		return compressed;
	}
}
