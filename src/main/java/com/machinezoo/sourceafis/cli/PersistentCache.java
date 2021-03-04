// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.io.*;
import java.nio.file.*;
import java.util.function.*;
import java.util.zip.*;
import org.apache.commons.io.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.cbor.*;
import com.machinezoo.noexception.*;
import com.machinezoo.sourceafis.*;

abstract class PersistentCache<T> implements Supplier<T> {
	static final Path home;
	static final Path output;
	static {
		/*
		 * First try XDG_* variables. Data directories may be in strange locations, for example inside flatpak.
		 */
		var configured = System.getenv("XDG_CACHE_HOME");
		Path root;
		if (configured != null)
			root = Paths.get(configured);
		else {
			/*
			 * Fall back to XDG_* default. This will perform poorly on Windows, but it will work.
			 */
			root = Paths.get(System.getProperty("user.home"), ".cache");
		}
		home = root.resolve("sourceafis");
		output = home.resolve("java").resolve(FingerprintCompatibility.version());
	}
	abstract T compute();
	private final Class<T> clazz;
	private final Path path;
	PersistentCache(Class<T> clazz, Object... identity) {
		this.clazz = clazz;
		var path = output;
		for (var component : identity) {
			if (component instanceof String)
				path = path.resolve((String)component);
			else if (component instanceof SampleFingerprint) {
				var fp = (SampleFingerprint)component;
				path = path.resolve(fp.dataset.name).resolve(fp.name());
			} else
				throw new IllegalArgumentException();
		}
		this.path = path.getParent().resolve(path.getFileName().toString() + ".cbor");
	}
	private static byte[] gzip(byte[] data) {
		return Exceptions.sneak().get(() -> {
			var buffer = new ByteArrayOutputStream();
			try (var gzip = new GZIPOutputStream(buffer)) {
				gzip.write(data);
			}
			return buffer.toByteArray();
		});
	}
	private static byte[] gunzip(byte[] compressed) {
		return Exceptions.sneak().get(() -> {
			try (var gzip = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
				return IOUtils.toByteArray(gzip);
			}
		});
	}
	private static final ObjectMapper mapper = new ObjectMapper(new CBORFactory())
		.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	@Override
	public T get() {
		return Exceptions.sneak().get(() -> {
			if (Files.exists(path))
				return mapper.readValue(gunzip(Files.readAllBytes(path)), clazz);
			T value = compute();
			Files.createDirectories(path.getParent());
			Files.write(path, gzip(mapper.writeValueAsBytes(value)));
			return value;
		});
	}
}
