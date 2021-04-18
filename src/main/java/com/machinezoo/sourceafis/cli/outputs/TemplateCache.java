// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class TemplateCache {
	public static byte[] load(Fingerprint fp) {
		return Cache.get(byte[].class, Paths.get("templates"), Cache.withExtension(fp.path(), ".cbor"), () -> {
			return new FingerprintTemplate(fp.decode()).toByteArray();
		});
	}
	public static FingerprintTemplate deserialize(Fingerprint fp) {
		return new FingerprintTemplate(load(fp));
	}
}
