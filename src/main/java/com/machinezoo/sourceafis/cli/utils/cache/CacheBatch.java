// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.cli.config.*;

public class CacheBatch {
	private final Path category;
	CacheBatch(Path category) {
		this.category = category;
	}
	public void add(Path identity, Object data) {
		Exceptions.sneak().run(() -> {
			var path = Configuration.output().resolve(category).resolve(identity);
			var serialization = CacheSerialization.select(data.getClass());
			path = serialization.rename(path);
			var compression = CacheCompression.select(path);
			path = compression.rename(path);
			Files.createDirectories(path.getParent());
			Files.write(path, compression.compress(serialization.serialize(data)));
		});
	}
}
