// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.datasets;

public enum Sample {
	FVC2000_1B("fvc2000-1b"),
	FVC2000_2B("fvc2000-2b"),
	FVC2000_3B("fvc2000-3b"),
	FVC2000_4B("fvc2000-4b"),
	FVC2002_1B("fvc2002-1b"),
	FVC2002_2B("fvc2002-2b"),
	FVC2002_3B("fvc2002-3b"),
	FVC2002_4B("fvc2002-4b"),
	FVC2004_1B("fvc2004-1b"),
	FVC2004_2B("fvc2004-2b"),
	FVC2004_3B("fvc2004-3b"),
	FVC2004_4B("fvc2004-4b");
	public final String name;
	Sample(String name) {
		this.name = name;
	}
	public double dpi() {
		switch (this) {
		case FVC2002_2B:
			return 569;
		case FVC2004_3B:
			return 512;
		default:
			return 500;
		}
	}
}
