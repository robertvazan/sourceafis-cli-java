// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;

interface CacheSerialization {
	Path rename(Path path);
	byte[] serialize(Object value);
	<T> T deserialize(byte[] serialized, Class<T> clazz);
}
