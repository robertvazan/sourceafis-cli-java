// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;
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
	private static final ConcurrentMap<Path, AtomicBoolean> reported = new ConcurrentHashMap<>();
	public static <T> T get(Class<T> clazz, Path category, Path identity, Consumer<CacheBatch> generator) {
		return Exceptions.sneak().get(() -> {
			var path = Configuration.output().resolve(category).resolve(identity);
			var serialization = CacheSerialization.select(clazz);
			path = serialization.rename(path);
			var compression = CacheCompression.select(path);
			path = compression.rename(path);
			if (!Files.exists(path)) {
				if (Configuration.baselineMode)
					throw new IllegalStateException("Baseline data was not found.");
				if (!reported.computeIfAbsent(category, c -> new AtomicBoolean()).getAndSet(true))
					logger.info("Computing {}...", category);
				generator.accept(new CacheBatch(category));
			}
			return serialization.deserialize(compression.decompress(Files.readAllBytes(path)), clazz);
		});
	}
	public static <T> T get(Class<T> clazz, Path category, Path identity, Supplier<T> supplier) {
		return get(clazz, category, identity, batch -> batch.add(identity, supplier.get()));
	}
}
