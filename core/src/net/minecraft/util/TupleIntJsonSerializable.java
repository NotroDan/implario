package net.minecraft.util;

public class TupleIntJsonSerializable<T extends IJsonSerializable> {

	private int integerValue;
	private T jsonSerializableValue;

	/**
	 * Gets the integer value stored in this tuple.
	 */
	public int getIntegerValue() {
		return this.integerValue;
	}

	/**
	 * Sets this tuple's integer value to the given value.
	 */
	public void setIntegerValue(int integerValueIn) {
		this.integerValue = integerValueIn;
	}

	public T getJsonSerializableValue() {
		return this.jsonSerializableValue;
	}

	/**
	 * Sets this tuple's JsonSerializable value to the given value.
	 */
	public void setJsonSerializableValue(T jsonSerializableValueIn) {
		this.jsonSerializableValue = jsonSerializableValueIn;
	}

}
