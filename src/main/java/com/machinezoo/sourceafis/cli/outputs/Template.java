// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.cbor.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.samples.*;
import com.machinezoo.sourceafis.cli.utils.*;

public class Template {
	public static byte[] serialized(Fingerprint fp) {
		return Cache.get(byte[].class, Paths.get("templates"), Cache.withExtension(fp.path(), ".cbor"), () -> {
			return new FingerprintTemplate(fp.decode()).toByteArray();
		});
	}
	public static FingerprintTemplate of(Fingerprint fp) {
		return new FingerprintTemplate(serialized(fp));
	}
	public static class ParsedTemplate {
		public String version;
		public int width;
		public int height;
		public int[] positionsX;
		public int[] positionsY;
		public double[] directions;
		public String types;
	}
	private static final ObjectMapper mapper = new ObjectMapper(new CBORFactory());
	public static ParsedTemplate parse(Fingerprint fp) {
		return Exceptions.sneak().get(() -> mapper.readValue(serialized(fp), ParsedTemplate.class));
	}
}
