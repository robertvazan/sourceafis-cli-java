// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.nio.file.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public record ChecksumCache(ChecksumOperation operation, Dataset dataset) implements PerDatasetCache<ChecksumTable> {
	@Override
	public Path category() {
		return Paths.get("checksums", operation.name());
	}
	@Override
	public Class<ChecksumTable> type() {
		return ChecksumTable.class;
	}
	@Override
	public ChecksumTable compute() {
		return operation.checksum(dataset);
	}
}
