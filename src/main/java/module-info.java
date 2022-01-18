// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
import com.machinezoo.stagean.*;

@ApiIssue("Rename 'match' to 'comparison'.")
@ApiIssue("Wrap library APIs (engine, transparency, visualization) in CLI commands. Inputs and outputs in files.")
@CodeIssue("Logger is overkill. Just print directly to stdout via Pretty class.")
@CodeIssue("Stats summing inspired by Rust's utils/statistics.")
@CodeIssue("Could use code structure hints from Rust (e.g. input package).")
@CodeIssue("Add MissingBaselineException (inspired by private forks).")
@DocIssue("Wrap description lines, so that descriptions can be longer.")
@DocIssue("Dedicated homepage for Java port of the CLI.")
/*
 * Open the module for unfettered serialization of persistent caches.
 * It's simpler to do it this way than to list all the packages with individual opens statements.
 */
open module com.machinezoo.sourceafis.cli {
	exports com.machinezoo.sourceafis.cli;
	/*
	 * JOL needs sun.misc.Unsafe.
	 */
	requires jdk.unsupported;
	requires java.desktop;
	requires com.machinezoo.stagean;
	requires com.machinezoo.noexception;
	requires com.machinezoo.sourceafis;
	requires org.slf4j;
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
