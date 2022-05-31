// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

import com.machinezoo.sourceafis.cli.utils.*;

class CborSerialization implements CacheSerialization {
	@Override
	public String extension() {
		return ".cbor";
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
