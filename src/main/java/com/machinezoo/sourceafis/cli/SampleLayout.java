//Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

import static java.util.stream.Collectors.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import org.apache.commons.io.*;
import com.machinezoo.noexception.*;
import one.util.streamex.*;

class SampleLayout {
	private final int[] offsets;
	private final int[] fingers;
	private final String[] names;
	private final String[] filenames;
	private final String[] prefixes;
	int fingers() {
		return offsets.length - 1;
	}
	int impressions(int finger) {
		return offsets[finger + 1] - offsets[finger];
	}
	int fingerprints() {
		return fingers.length;
	}
	int fingerprint(int finger, int impression) {
		if (impression < 0 || impression >= impressions(finger))
			throw new IndexOutOfBoundsException();
		return offsets[finger] + impression;
	}
	int finger(int fp) {
		return fingers[fp];
	}
	int impression(int fp) {
		return fp - offsets[finger(fp)];
	}
	String name(int fp) {
		return names[fp];
	}
	String filename(int fp) {
		return filenames[fp];
	}
	String prefix(int finger) {
		return prefixes[finger];
	}
	private static final Pattern PATTERN = Pattern.compile("(.+)_[0-9]+\\.(?:tif|tiff|png|bmp|jpg|jpeg|wsq)");
	@SuppressWarnings("resource")
	private SampleLayout(Path directory) {
		var groups = new HashMap<String, List<String>>();
		for (var path : Exceptions.sneak().get(() -> Files.list(directory).collect(toList()))) {
			var filename = path.getFileName().toString();
			var matcher = PATTERN.matcher(filename);
			if (matcher.matches())
				groups.computeIfAbsent(matcher.group(1), g -> new ArrayList<>()).add(filename);
		}
		if (groups.isEmpty())
			throw new IllegalStateException("Empty dataset.");
		if (groups.size() == 1)
			throw new IllegalStateException("Found only one finger in the dataset.");
		if (groups.values().stream().noneMatch(l -> l.size() > 1))
			throw new IllegalStateException("Found only one impression per finger in the dataset.");
		prefixes = new String[groups.size()];
		names = new String[groups.values().stream().mapToInt(l -> l.size()).sum()];
		filenames = new String[names.length];
		offsets = new int[prefixes.length + 1];
		fingers = new int[names.length];
		int finger = 0;
		int fp = 0;
		for (var prefix : StreamEx.of(groups.keySet()).sorted()) {
			prefixes[finger] = prefix;
			offsets[finger + 1] = offsets[finger] + groups.get(prefix).size();
			for (var filename : StreamEx.of(groups.get(prefix)).sorted()) {
				filenames[fp] = filename;
				names[fp] = FilenameUtils.removeExtension(filename);
				fingers[fp] = finger;
				++fp;
			}
			++finger;
		}
	}
	static SampleLayout scan(String dataset) {
		return new SampleLayout(SampleDownload.unpack(dataset));
	}
}
