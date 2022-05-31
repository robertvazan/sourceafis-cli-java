// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.outputs;

import java.nio.file.*;
import java.util.*;
import com.machinezoo.sourceafis.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;
import one.util.streamex.*;

public record TemplateCache(Dataset dataset) implements MapCache<Fingerprint, byte[]> {
	@Override
	public Path category() {
		return Paths.get("templates");
	}
	@Override
	public Path sector() {
		return dataset().path();
	}
	@Override
	public Path identity(Fingerprint fp) {
		return fp.path();
	}
	@Override
	public Class<byte[]> type() {
		return byte[].class;
	}
	@Override
	public String extension() {
		return ".cbor";
	}
	@Override
	public void populate(CacheWriter<Fingerprint, byte[]> writer) {
		var decoded = new DecodedImageCache(dataset).load();
		dataset.fingerprints().parallelStream().forEach(fp -> writer.put(fp, new FingerprintTemplate(decoded.get(fp)).toByteArray()));
	}
	public Map<Fingerprint, byte[]> toMap() {
		var map = new HashMap<Fingerprint, byte[]>();
		var loaded = load();
		for (var fp : dataset().fingerprints())
			map.put(fp, loaded.get(fp));
		return map;
	}
	public Map<Fingerprint, FingerprintTemplate> deserialize() {
		var serialized = load();
		return StreamEx.of(dataset.fingerprints()).mapToEntry(fp -> new FingerprintTemplate(serialized.get(fp))).toMap();
	}
	public static Map<Fingerprint, byte[]> toMap(Profile profile) {
		return StreamEx.of(profile.datasets()).flatMapToEntry(ds -> new TemplateCache(ds).toMap()).toMap();
	}
	public static Map<Fingerprint, FingerprintTemplate> deserialize(Profile profile) {
		return StreamEx.of(profile.datasets()).flatMapToEntry(ds -> new TemplateCache(ds).deserialize()).toMap();
	}
}
