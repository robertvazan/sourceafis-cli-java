// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;

class TrivialSerialization implements CacheSerialization {
	@Override
	public Path rename(Path path) {
		return path;
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
