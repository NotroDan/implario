package net.minecraft.resources.override;

public abstract class Mapping<T> {

	public final int id;
	public final String address;
	public final T overridden;
	public final T actual;

	public Mapping(int id, String address, T overridden, T actual) {
		this.id = id;
		this.address = address;
		this.overridden = overridden;
		this.actual = actual;
	}

	/**
	 * Код, который должен выполняться при мапе/анмапе этого маппинга,
	 * находится в реализации этого метода
	 *
	 * @param id      Циферный ID для легаси-маппингов (блоки, итемы, энтити)
	 * @param address Буквенный ID, лучше использовать его
	 * @param element Элемент, который нужно замаппить. Может быть null, тогда элемент надо удалить.
	 */
	public abstract void map(int id, String address, T element);

	public void map() {
		map(id, address, actual);
	}

	public void undo() {
		map(id, address, overridden);
	}

}