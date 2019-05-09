package net.minecraft.util;

import static java.lang.Math.*;
import static net.minecraft.util.EasingFunction.*;

public enum Easing {

	QUAD_I(x -> x * x),
	QUAD_O(x -> 1 - (1 - x) * (1 - x)),
	QUAD_B(x -> x < 0.5 ?
			2 * x * x :
			1 - pow(-2 * x + 2, 2) / 2),

	CUBE_I(x -> x * x * x),
	CUBE_O(x -> 1 - pow (1 - x, 3)),
	CUBE_B(x -> x < 0.5 ?
				4 * x * x * x :
				1 - pow(-2 * x + 2, 3) / 2),

	QUAR_I(x -> x * x * x * x),
	QUAR_O(x -> 1 - pow(1 - x, 4)),
	QUAR_B(x -> x < 0.5 ?
			8 * x * x * x * x :
			1 - pow(-2 * x + 2, 4) / 2),

	QUIN_I(x -> x * x * x * x * x),
	QUIN_O(x -> 1 - Math.pow(1 - x, 5)),
	QUIN_B(x -> x < 0.5 ?
				16 * x * x * x * x * x :
				1 - pow(-2 * x + 2, 5) / 2),

	SINS_I(x -> 1 - cos(x * PI / 2)),
	SINS_O(x -> sin(x * PI / 2)),
	SINS_B(x -> -(cos(PI * x) - 1) / 2),

	CIRC_I(x -> 1 - sqrt(1 - pow(x, 2))),
	CIRC_O(x -> sqrt(1 - pow(x - 1, 2))),
	CIRC_B(x -> x < 0.5 ?
			(1 - sqrt(1 - pow(2 * x, 2))) / 2 :
			(sqrt(1 - pow(-2 * x + 2, 2)) + 1) / 2),

	ELAS_I(x -> x == 0 ? 0 : x == 1 ? 1 :
			-pow(2, 10 * x - 10) * sin((x * 10 - 10.75) * c4)),
	ELAS_O(x -> x == 0 ? 0 : x == 1 ? 1 :
			pow(2, -10 * x) * sin((x * 10 - 0.75) * c4) + 1),
	ELAS_B(x -> x == 0 ? 0 : x == 1 ? 1 : x < 0.5 ?
			-(pow(2, 20 * x - 10) * sin((20 * x - 11.125) * c5)) / 2 :
			pow(2, -20 * x + 10) * sin((20 * x - 11.125) * c5) / 2 + 1),

	BACK_I(x -> c3 * x * x * x - c1 * x * x),
	BACK_O(x -> 1 + c3 * pow(x - 1, 3) + c1 * pow(x - 1, 2)),
	BACK_B(x -> x < 0.5 ?
			pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2) / 2 :
			(pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2),

	BOUN_O(x -> {
		double n1 = 7.5625, d1 = 2.75;
		if (x < 1 / d1) return n1 * x * x;
		if (x < 2 / d1) return n1 * (x -= 1.5 / d1) * x + .75;
		if (x < 2.5 / d1) return n1 * (x -= 2.25 / d1) * x + .9375;
		return n1 * (x -= 2.625 / d1) * x + .984375;
	}),
	BOUN_I(x -> 1 - BOUN_O.f.ease(1 - x)),
	BOUN_B(x -> x < 0.5 ?
			(1 - BOUN_O.f.ease(1 - 2 * x)) / 2 :
			(1 + BOUN_O.f.ease(2 * x - 1)) / 2),

	;

	private final EasingFunction f;

	Easing(EasingFunction f) {
		this.f = f;
	}

	public double ease(double x) {
		return f.ease(x);
	}

}
