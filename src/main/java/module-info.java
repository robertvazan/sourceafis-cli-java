// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
import com.machinezoo.stagean.*;

@ApiIssue("Wrap library APIs (engine, transparency, visualization) in CLI commands. Inputs and outputs in files.")
@DocIssue("Dedicated homepage for Java port of the CLI.")
/*
 * Jackson still requires the module to be open to serialize records.
 * https://github.com/FasterXML/jackson-databind/issues/3352
 */
open module com.machinezoo.sourceafis.cli {
	exports com.machinezoo.sourceafis.cli;
	/*
	 * JOL needs sun.misc.Unsafe.
	 */
	requires jdk.unsupported;
	/*
	 * Needed for ImageIO used during grayscale export.
	 */
	requires java.desktop;
	/*
	 * Needed for image dataset download.
	 */
	requires java.net.http;
	requires com.machinezoo.stagean;
	requires com.machinezoo.noexception;
	requires com.machinezoo.sourceafis;
	requires it.unimi.dsi.fastutil;
	requires one.util.streamex;
	requires org.apache.commons.lang3;
	requires org.apache.commons.io;
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.dataformat.cbor;
	/*
	 * JOL library is not a module and there's no way to request this from JOL maintainers.
	 */
	requires jol.core;
}
