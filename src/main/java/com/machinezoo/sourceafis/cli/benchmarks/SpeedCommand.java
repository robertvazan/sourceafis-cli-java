// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;
import one.util.streamex.*;

public record SpeedCommand(SpeedCache<?> cache) implements SimpleCommand {
	@Override
	public List<String> subcommand() {
		return List.of("benchmark", "speed", cache.name());
	}
	@Override
	public String description() {
		return cache.description();
	}
	@Override
	public void run() {
		MissingBaselineException.silence().run(() -> {
			var all = cache.get().skip(SpeedCache.WARMUP);
			var global = TimingSummary.sum(StreamEx.of(all.segments().values()).flatArray(a -> a).toList());
			Pretty.print("Gross speed: " + Pretty.speed(global.count() / (double)SpeedCache.NET_DURATION, "gross"));
			var table = new SpeedTable("Dataset");
			for (var profile : Profile.all())
				table.add(profile.name(), all.narrow(profile));
			table.print();
		});
	}
}
