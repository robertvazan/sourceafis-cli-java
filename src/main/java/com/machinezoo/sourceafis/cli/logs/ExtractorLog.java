// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.logs;

import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.checksums.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class ExtractorLog extends TransparencyLog<Fingerprint> {
	@Override
	public String name() {
		return "extractor";
	}
	@Override
	protected TransparencyChecksum<Fingerprint> checksum() {
		return new ExtractorChecksum();
	}
	@Override
	protected byte[] log(String key, Fingerprint fp, int index, int count, String mime) {
		return Cache.get(byte[].class, category(key), identity(key, fp, index, count, mime), map -> {
			log(key, fp, index, count, mime, () -> new FingerprintTemplate(fp.decode()), map);
		});
	}
}
