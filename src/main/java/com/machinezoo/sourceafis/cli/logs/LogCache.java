// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.logs;

import java.nio.file.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public record LogCache(LogOperation operation, Dataset dataset, String key, boolean normalized) implements FileCache {
	@Override
	public Path category() {
		if (normalized)
			return Paths.get("logs", "normalized", operation.name(), key);
		else
			return Paths.get("logs", operation.name(), key);
	}
	@Override
	public Path sector() {
		return dataset.path();
	}
	@Override
	public void populate(CacheWriter<Path, byte[]> writer) {
		operation.log(dataset, new LogWriter() {
			@Override
			public void put(Path path, Runnable action) {
				try (var collector = new LogCollector(key)) {
					action.run();
					for (int i = 0; i < collector.files.size(); ++i) {
						var numbered = collector.files.size() == 1 ? path : path.resolve(Integer.toString(i));
						var data = normalized ? Serializer.normalize(collector.mime, collector.files.get(i)) : collector.files.get(i);
						writer.put(Pretty.path(numbered, Pretty.extension(collector.mime)), data);
					}
				}
			}
		});
	}
}
