// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.outputs;

import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.*;

public record ParsedTemplate(
	String version,
	int width,
	int height,
	int[] positionsX,
	int[] positionsY,
	double[] directions,
	String types) {
	public static ParsedTemplate parse(Fingerprint fp) {
		return Serializer.deserialize(TemplateCache.load(fp), ParsedTemplate.class);
	}
}
