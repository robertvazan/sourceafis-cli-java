// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

public class Main {
	public static void main(String args[]) {
		if (args.length != 1)
			return;
		switch (args[0]) {
		case "extractor-transparency-stats":
			TransparencyStats.report(TransparencyStats.extractorTable());
			break;
		}
	}
}
