// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
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
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.dataformat.cbor;
	requires java.desktop;
	requires com.machinezoo.noexception;
	requires com.machinezoo.sourceafis;
	requires it.unimi.dsi.fastutil;
	requires one.util.streamex;
	requires org.apache.commons.io;
	requires org.apache.commons.lang3;
	requires org.slf4j;
	/*
	 * JOL library is not a module and there's no way to request this from JOL maintainers.
	 */
	requires jol.core;
}
