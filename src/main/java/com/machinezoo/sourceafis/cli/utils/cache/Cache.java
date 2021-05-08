// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import org.slf4j.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.cli.config.*;

public class Cache<T> {
	private static final Logger logger = LoggerFactory.getLogger(Cache.class);
	public static Path withExtension(Path path, String extension) {
		return path.resolveSibling(path.getFileName() + extension);
	}
	private static CacheSerialization serialization(Class<?> clazz) {
		if (clazz == byte[].class)
			return new TrivialSerialization();
		return new CborSerialization();
	}
	private static CacheCompression compression(Path path) {
		if (path.getFileName().toString().endsWith(".cbor"))
			return new GzipCompression();
		return new TrivialCompression();
	}
	private static final ConcurrentMap<Path, AtomicBoolean> reported = new ConcurrentHashMap<>();
	public static <T> T get(Class<T> clazz, Path category, Path identity, Consumer<Map<Path, Object>> generator) {
		return Exceptions.sneak().get(() -> {
			var deserialization = serialization(clazz);
			var decompression = compression(deserialization.rename(identity));
			var path = decompression.rename(deserialization.rename(Configuration.output().resolve(category).resolve(identity)));
			if (!Files.exists(path)) {
				if (Configuration.baselineMode)
					throw new IllegalStateException("Baseline data was not found.");
				if (!reported.computeIfAbsent(category, c -> new AtomicBoolean()).getAndSet(true))
					logger.info("Computing {}...", category);
				var collection = new HashMap<Path, Object>();
				generator.accept(collection);
				for (var key : collection.keySet()) {
					var value = collection.get(key);
					var serialization = serialization(value.getClass());
					var compression = compression(serialization.rename(key));
					var destination = compression.rename(serialization.rename(Configuration.output().resolve(category).resolve(key)));
					Files.createDirectories(destination.getParent());
					Files.write(destination, compression.compress(serialization.serialize(value)));
				}
			}
			return deserialization.deserialize(decompression.decompress(Files.readAllBytes(path)), clazz);
		});
	}
	public static <T> T get(Class<T> clazz, Path category, Path identity, Supplier<T> supplier) {
		return get(clazz, category, identity, map -> map.put(identity, supplier.get()));
	}
}
