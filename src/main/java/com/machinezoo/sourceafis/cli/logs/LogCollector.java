// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.logs;

import java.util.*;
import com.machinezoo.sourceafis.*;

public class LogCollector extends FingerprintTransparency {
	public final String key;
	public String mime;
	public final List<byte[]> files = new ArrayList<>();
	public LogCollector(String key) {
		this.key = key;
	}
	@Override
	public boolean accepts(String key) {
		return this.key.equals(key);
	}
	@Override
	public void take(String key, String mime, byte[] data) {
		this.mime = mime;
		files.add(data);
	}
}
