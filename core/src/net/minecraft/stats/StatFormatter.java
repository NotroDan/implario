package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

@FunctionalInterface
public interface StatFormatter {

	NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.US);
	DecimalFormat decimalFormat = new DecimalFormat("########0.00");
	StatFormatter simpleFormat = value -> numberFormat.format((long) value);
	StatFormatter damageFormat = value -> decimalFormat.format((double) value * 0.1D);
	StatFormatter distanceFormat = value -> {
		double meters = (double) value / 100.0D;
		double kilometers = meters / 1000.0D;
		return kilometers > 0.5D ? decimalFormat.format(kilometers) + " km" :
				meters > 0.5D ? decimalFormat.format(meters) + " m" :
						value + " cm";
	};
	StatFormatter timeFormat = value -> {
		double seconds = (double) value / 20.0D;
		double minutes = seconds / 60.0D;
		double hours = minutes / 60.0D;
		double days = hours / 24.0D;
		double years = days / 365.0D;
		return years > 0.5D ? decimalFormat.format(years) + " y" :
				days > 0.5D ? decimalFormat.format(days) + " d" :
						hours > 0.5D ? decimalFormat.format(hours) + " h" :
								minutes > 0.5D ? decimalFormat.format(minutes) + " m" :
										seconds + " s";
	};

	/**
	 * Форматирует заданное число в человекочитаемый вид.
	 */
	String format(int value);


}
