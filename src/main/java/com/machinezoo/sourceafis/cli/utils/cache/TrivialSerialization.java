// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

class TrivialSerialization implements CacheSerialization {
	@Override
	public String extension() {
		return "";
	}
	@Override
	public byte[] serialize(Object value) {
		return (byte[])value;
	}
	@Override
	@SuppressWarnings("unchecked")
	public <T> T deserialize(byte[] serialized, Class<T> clazz) {
		return (T)serialized;
	}
}
