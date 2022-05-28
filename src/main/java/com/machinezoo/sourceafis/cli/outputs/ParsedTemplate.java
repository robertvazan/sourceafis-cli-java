// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.outputs;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.cbor.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.cli.datasets.*;

public record ParsedTemplate(
	String version,
	int width,
	int height,
	int[] positionsX,
	int[] positionsY,
	double[] directions,
	String types) {
	private static final ObjectMapper mapper = new ObjectMapper(new CBORFactory());
	public static ParsedTemplate parse(Fingerprint fp) {
		return Exceptions.sneak().get(() -> mapper.readValue(TemplateCache.load(fp), ParsedTemplate.class));
	}
}
