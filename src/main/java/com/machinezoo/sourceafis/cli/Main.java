// Part of SourceAFIS for Java CLI: https://sourceafis.machinezoo.com/java
package com.machinezoo.sourceafis.cli;

public class Main {
	public static void main(String args[]) {
		if (args.length < 1)
			return;
		switch (args[0]) {
		case "png":
			ImageConversion.png();
			break;
		case "gray":
			ImageConversion.gray();
			break;
		case "footprint":
			TemplateFootprint.report();
			break;
		case "accuracy":
			ScalarAccuracy.report();
			break;
		case "extractor-transparency-stats":
			TransparencyStats.report(TransparencyStats.extractorTable());
			break;
		case "extractor-transparency-files":
			if (args.length < 2)
				return;
			TransparencyFile.extractor(args[1]);
			break;
		case "normalized-extractor-transparency-files":
			if (args.length < 2)
				return;
			TransparencyFile.extractorNormalized(args[1]);
			break;
		}
	}
}
