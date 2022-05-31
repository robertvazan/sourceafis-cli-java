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
				try (var logger = new LogCollector(key)) {
					action.run();
					if (logger.mime != null) {
						if (logger.files.size() == 1)
							writer.put(path.resolveSibling(path.getFileName().toString() + Pretty.extension(logger.mime)), logger.files.get(0));
						else {
							for (int i = 0; i < logger.files.size(); ++i)
								writer.put(path.resolve(i + Pretty.extension(logger.mime)), logger.files.get(i));
						}
					}
				}
			}
		});
	}
}
