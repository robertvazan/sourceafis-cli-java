// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.logs;

import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.checksums.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class ExtractorLog extends TransparencyLog<Fingerprint> {
	@Override
	public String name() {
		return "extractor";
	}
	@Override
	public String description() {
		return "Log extractor transparency data for given key.";
	}
	@Override
	protected TransparencyChecksum<Fingerprint> checksum() {
		return new ExtractorChecksum();
	}
	@Override
	protected byte[] log(String key, Fingerprint fp, int index, int count, String mime) {
		return Cache.get(byte[].class, category(key), fp.path(), identity(fp, index, count, mime), batch -> {
			log(key, fp, count, mime, () -> new FingerprintTemplate(fp.decode()), batch);
		});
	}
}
