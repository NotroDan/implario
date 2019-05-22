package net.minecraft.resources.event;

import net.minecraft.resources.Domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Библиотека со списками слушателей событий.
 * По классу события можно получить список слушателей событий этого класса.
 * @param <D> Тип данных (Event, Packet).
 */
public class HandlerLibrary<D extends Listenable> {

	private final List<Entry> list = new ArrayList<>();

	public <T extends D> List<Cell<T>> getListeners(Class<T> clazz) {
		for (Entry e : list) if (e.clazz == clazz) return e.listeners;
		Entry<T> e = new Entry(clazz);
		list.add(e);
		return e.listeners;
	}

	public <T extends D> void register(Domain domain, Class<T> type, Handler<D, T> handler) {
		getListeners(type).add(new Cell<>(handler, domain));
	}

	public void disable(Domain domain) {
		for (Entry entry : list) {
			List<Cell> toRemove = new ArrayList<>();
			for (Cell cell : (List<Cell>) entry.listeners)
				if (cell.domain == domain) toRemove.add(cell);
			entry.listeners.removeAll(toRemove);
		}
	}

	private class Entry<T extends D> {

		final Class<T> clazz;
		final List<Cell<T>> listeners = new ArrayList<>();

		Entry(Class<T> clazz) {
			this.clazz = clazz;
		}

	}

	public class Cell<T extends D> {
		private final Handler<D, T> handler;
		private final Domain domain;

		private Cell(Handler<D, T> handler, Domain domain) {
			this.handler = handler;
			this.domain = domain;
		}

		public Handler<D, T> getHandler() {
			return handler;
		}

	}

}
