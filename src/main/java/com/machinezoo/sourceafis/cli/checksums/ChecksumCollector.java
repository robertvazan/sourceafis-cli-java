// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.*;

public class ChecksumCollector extends FingerprintTransparency {
	public final List<TransparencyTable> records = new ArrayList<>();
	@Override
	public void take(String key, String mime, byte[] data) {
		records.add(TransparencyTable.solo(key, mime, data));
	}
	public static TransparencyTable collect(Runnable action) {
		try (var transparency = new ChecksumCollector()) {
			action.run();
			return TransparencyTable.sum(transparency.records);
		}
	}
}
