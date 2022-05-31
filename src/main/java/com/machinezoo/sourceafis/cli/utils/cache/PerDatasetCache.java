// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.utils.cache;

import java.nio.file.*;
import com.machinezoo.sourceafis.cli.inputs.*;

public interface PerDatasetCache<V> extends SoloCache<V> {
	Dataset dataset();
	@Override
	default Path sector() {
		return dataset().path();
	}
}
