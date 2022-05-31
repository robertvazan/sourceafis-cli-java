// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.checksums;

import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.*;

public interface ChecksumOperation {
	String name();
	String description();
	ChecksumTable checksum(Dataset dataset);
	default ChecksumTable sum(Profile profile) {
		return ChecksumTable.sum(profile.datasets().stream().map(ds -> new ChecksumCache(this, ds).get()).toList());
	}
	default byte[] global() {
		var hash = new Hasher();
		for (var row : sum(Profile.everything()).rows())
			if (!row.key().equals("version"))
				hash.add(row.stats().hash());
		return hash.compute();
	}
}
