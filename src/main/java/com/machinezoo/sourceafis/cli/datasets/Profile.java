// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

import java.util.*;
import one.util.streamex.*;

public class Profile {
	public final SampleProfile kind;
	public final ImageFormat format;
	public Profile(SampleProfile kind, ImageFormat format) {
		this.kind = kind;
		this.format = format;
	}
	public String name() {
		return kind.name();
	}
	public static Profile everything() {
		return new Profile(new AllSamples(), ImageFormat.DEFAULT);
	}
	public static List<Profile> aggregate() {
		return StreamEx.of(SampleProfile.aggregate()).map(sp -> new Profile(sp, ImageFormat.DEFAULT)).toList();
	}
	public static List<Profile> all() {
		return StreamEx.of(SampleProfile.all()).map(sp -> new Profile(sp, ImageFormat.DEFAULT)).toList();
	}
	public List<Dataset> datasets() {
		return StreamEx.of(kind.samples()).map(s -> new Dataset(s, format)).toList();
	}
	public List<Fingerprint> fingerprints() {
		return StreamEx.of(datasets()).flatCollection(ds -> ds.fingerprints()).toList();
	}
}
