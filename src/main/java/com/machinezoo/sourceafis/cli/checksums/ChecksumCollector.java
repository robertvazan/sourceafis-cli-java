// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.*;

public class ChecksumCollector extends FingerprintTransparency {
	public final List<ChecksumTable> records = new ArrayList<>();
	@Override
	public void take(String key, String mime, byte[] data) {
		records.add(ChecksumTable.solo(key, mime, data));
	}
	public static ChecksumTable collect(Runnable action) {
		try (var transparency = new ChecksumCollector()) {
			action.run();
			return ChecksumTable.sum(transparency.records);
		}
	}
}
