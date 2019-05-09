package net.minecraft.util;

import static java.lang.Math.PI;

@FunctionalInterface
public interface EasingFunction {


	double
			c1 = 1.70158,
			c2 = c1 * 1.525,
			c3 = c1 + 1,
			c4 = 2 * PI / 3,
			c5 = 2 * PI / 4.5;

	double ease(double x);

}
