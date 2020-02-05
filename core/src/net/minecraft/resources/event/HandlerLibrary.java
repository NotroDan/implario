package net.minecraft.resources.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Библиотека со списками слушателей событий.
 * По классу события можно получить список слушателей событий этого класса.
 *
 * @param <D> Тип данных (Event, Packet).
 */
public class HandlerLibrary<D> {

	private final List<Entry> list = new ArrayList<>();

	public <T extends D> List<Cell<T>> getListeners(Class<T> clazz) {
		for (Entry e : list) if (e != null && e.clazz == clazz) return e.listeners;
		Entry<T> e = new Entry<>(clazz);
		list.add(e);
		return e.listeners;
	}

	public <T extends D> void register(String domain, Class<T> type, Handler<D, T> handler) {
		getListeners(type).add(new Cell<>(handler, domain));
	}

	public void disable(String domain) {
		for (Entry entry : list) {
			List<Cell> toRemove = new ArrayList<>();
			for (Cell cell : (List<Cell>) entry.listeners)
				if (cell.domain.equals(domain)) toRemove.add(cell);
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
		private final String domain;

		private Cell(Handler<D, T> handler, String domain) {
			this.handler = handler;
			this.domain = domain;
		}

		public Handler<D, T> getHandler() {
			return handler;
		}

	}

}
