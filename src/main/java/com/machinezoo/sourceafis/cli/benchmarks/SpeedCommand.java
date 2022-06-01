// Part of SourceAFIS CLI for Java: https://sourceafis.machinezoo.com/cli
package com.machinezoo.sourceafis.cli.benchmarks;

import java.util.*;
import com.machinezoo.sourceafis.cli.inputs.*;
import com.machinezoo.sourceafis.cli.utils.args.*;
import com.machinezoo.sourceafis.cli.utils.cache.*;

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
			var data = cache.get();
			var warm = data.warmup();
			var table = new SpeedTable("Dataset");
			for (var profile : Profile.all())
				table.add(profile.name(), warm.narrow(profile), data);
			table.print();
		});
	}
}
