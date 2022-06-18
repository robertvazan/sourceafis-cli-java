// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.inputs;

import static java.util.stream.Collectors.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import org.apache.commons.io.*;
import com.machinezoo.noexception.*;
import one.util.streamex.*;

public class DatasetLayout {
	private final int[] offsets;
	private final int[] fingers;
	private final String[] names;
	private final String[] filenames;
	private final String[] prefixes;
	public int fingers() {
		return offsets.length - 1;
	}
	public int impressions(int finger) {
		return offsets[finger + 1] - offsets[finger];
	}
	public int fingerprints() {
		return fingers.length;
	}
	public int fingerprint(int finger, int impression) {
		if (impression < 0 || impression >= impressions(finger))
			throw new IndexOutOfBoundsException();
		return offsets[finger] + impression;
	}
	public int finger(int fp) {
		return fingers[fp];
	}
	public int impression(int fp) {
		return fp - offsets[finger(fp)];
	}
	public String name(int fp) {
		return names[fp];
	}
	public String filename(int fp) {
		return filenames[fp];
	}
	public String prefix(int finger) {
		return prefixes[finger];
	}
	private static final Pattern PATTERN = Pattern.compile("(.+)_[0-9]+\\.gray");
	@SuppressWarnings("resource")
	public DatasetLayout(GrayscaleImageCache cache) {
		var directory = cache.load().directory();
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
	private static final ConcurrentMap<Dataset, DatasetLayout> all = new ConcurrentHashMap<>();
	public static DatasetLayout get(Dataset dataset) {
		return all.computeIfAbsent(dataset, ds -> new DatasetLayout(new GrayscaleImageCache(dataset)));
	}
}
