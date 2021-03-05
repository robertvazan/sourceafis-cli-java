// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.nio.file.*;
import com.machinezoo.sourceafis.*;

class NativeTemplate {
	static byte[] serialized(SampleFingerprint fp) {
		return PersistentCache.get(byte[].class, Paths.get("templates"), PersistentCache.withExtension(fp.path(), ".cbor"), () -> {
			return new FingerprintTemplate(fp.decode()).toByteArray();
		});
	}
	static FingerprintTemplate of(SampleFingerprint fp) {
		return new FingerprintTemplate(serialized(fp));
	}
}
