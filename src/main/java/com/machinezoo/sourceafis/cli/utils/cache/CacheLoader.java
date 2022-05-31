// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;
import java.util.concurrent.*;
import java.util.function.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.cli.config.*;
import com.machinezoo.sourceafis.cli.utils.*;

class CacheLoader {
	private static final ConcurrentMap<Path, Object> reported = new ConcurrentHashMap<>();
	static <K, V> LoadedCache<K, V> load(MapCache<K, V> cache) {
		var category = cache.category();
		var sector = cache.sector();
		var directory = cache.root().resolve(cache.category()).resolve(cache.sector());
		var marker = directory.resolve("done");
		var serialization = CacheSerialization.select(cache.type());
		var compression = CacheCompression.select(cache.extension() + serialization.extension());
		var extension = cache.extension() + serialization.extension() + compression.extension();
		Function<K, Path> resolver = key -> {
			var identity = cache.identity(key);
			var path = directory.resolve(identity.startsWith(sector) ? sector.relativize(identity) : identity);
			return path.resolveSibling(path.getFileName() + extension);
		};
		if (!Files.exists(marker)) {
			if (Configuration.baselineMode)
				throw new MissingBaselineException(category);
			var flag = new Object();
			if (flag == reported.computeIfAbsent(category, c -> flag))
				Pretty.format("{0} {1}...", cache.action(), category);
			cache.populate(new CacheWriter<K, V>() {
				@Override
				public void put(K key, V value) {
					var path = resolver.apply(key);
					Exceptions.sneak().run(() -> {
						Files.createDirectories(path.getParent());
						Files.write(path, compression.compress(serialization.serialize(value)));
					});
				}
			});
		}
		return new LoadedCache<K, V>() {
			@Override
			public Path directory() {
				return directory;
			}
			@Override
			public V get(K key) {
				var path = resolver.apply(key);
				return Exceptions.sneak().get(() -> serialization.deserialize(compression.decompress(Files.readAllBytes(path)), cache.type()));
			}
		};
	}
}
