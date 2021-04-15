// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.checksums;

import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

public class ExtractorChecksum extends TransparencyChecksum<Fingerprint> {
	@Override
	public String name() {
		return "extractor";
	}
	@Override
	public List<Fingerprint> ids() {
		return Fingerprint.all();
	}
	@Override
	protected TransparencyTable checksum(Fingerprint fp) {
		return Cache.get(TransparencyTable.class, category(), fp.path(), () -> {
			return ChecksumCollector.collect(() -> new FingerprintTemplate(fp.decode()));
		});
	}
}
