package net.minecraft.util.functional;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ArrayUtils {

	public String[] dropFirstArg(String[] args) {
		if (args.length == 0) return args;
		return slice(args, new String[args.length - 1], 1);
	}

	public <T> T[] slice(T[] srcArray, T[] dstArray, int start) {
		System.arraycopy(srcArray, start, dstArray, 0, dstArray.length);
		return dstArray;
	}

}
