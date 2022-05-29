// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;
import java.util.concurrent.*;
import java.util.function.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.cli.config.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class Cache<T> {
	public static Path withExtension(Path path, String extension) {
		return path.resolveSibling(path.getFileName() + extension);
	}
	private static final Once<Path> reported = new Once<>();
	private static final ConcurrentMap<Path, Object> locks = new ConcurrentHashMap<>();
	public static <T> T get(Class<T> clazz, Path category, Path group, Path identity, Consumer<CacheBatch> generator) {
		return Exceptions.sneak().get(() -> {
			var path = Configuration.output().resolve(category).resolve(identity);
			var serialization = CacheSerialization.select(clazz);
			path = serialization.rename(path);
			var compression = CacheCompression.select(path);
			path = compression.rename(path);
			var lockId = Configuration.output().resolve(category).resolve(group);
			var lock = locks.computeIfAbsent(lockId, id -> new Object());
			synchronized (lock) {
				if (!Files.exists(path)) {
					var cacheId = category;
					if (Configuration.baselineMode)
						throw new MissingBaselineException(cacheId);
					if (reported.first(cacheId))
						Pretty.format("Computing {0}...", cacheId);
					generator.accept(new CacheBatch(category));
				}
				/*
				 * If no exception was thrown, the cache is now fully populated.
				 * Future operations will be read-only, which means they don't need locking.
				 * It is therefore okay to break locking by discarding the lock.
				 * Discarding locks is good for memory consumption.
				 */
				locks.remove(lockId);
			}
			return serialization.deserialize(compression.decompress(Files.readAllBytes(path)), clazz);
		});
	}
	public static <T> T get(Class<T> clazz, Path category, Path identity, Supplier<T> supplier) {
		return get(clazz, category, identity, identity, batch -> batch.add(identity, supplier.get()));
	}
}
