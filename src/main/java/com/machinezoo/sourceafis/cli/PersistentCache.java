// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import java.nio.file.*;
import java.util.function.*;
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
	private static final ObjectMapper mapper = new ObjectMapper(new CBORFactory())
		.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	@Override
	public T get() {
		return Exceptions.sneak().get(() -> {
			if (Files.exists(path))
				return mapper.readValue(Files.readAllBytes(path), clazz);
			T value = compute();
			Files.createDirectories(path.getParent());
			Files.write(path, mapper.writeValueAsBytes(value));
			return value;
		});
	}
}
