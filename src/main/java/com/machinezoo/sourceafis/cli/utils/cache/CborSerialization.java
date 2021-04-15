// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;
import com.machinezoo.sourceafis.cli.utils.*;

class CborSerialization implements CacheSerialization {
	@Override
	public Path rename(Path path) {
		return Cache.withExtension(path, ".cbor");
	}
	@Override
	public byte[] serialize(Object value) {
		return Serializer.serialize(value);
	}
	@Override
	public <T> T deserialize(byte[] serialized, Class<T> clazz) {
		return Serializer.deserialize(serialized, clazz);
	}
}
