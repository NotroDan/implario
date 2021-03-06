package net.minecraft.logging;

import java.util.Date;

@FunctionalInterface
public interface ILogInterceptor {

	void intercept(LogLevel level, Date date, String message);

}
