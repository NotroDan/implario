package net.minecraft.entity.player;

import net.minecraft.logging.Log;
import net.minecraft.resources.Domain;

import java.util.ArrayList;
import java.util.List;

public class PlayerGuiBridge {

	public interface GuiOpener<D> {
		void open(EntityPlayer p, D gui, boolean serverSide);
	}

	private static final List<Entry> list = new ArrayList<>();

	public static <T> void open(EntityPlayer p, Class<T> type, T gui, boolean serverSide) {
		GuiOpener<T> opener = getOpener(type);
		if (opener != null) opener.open(p, gui, serverSide);
		else Log.MAIN.warn("Для типа '" + type + "' не найдено слушателя!");
	}

	private static <T> GuiOpener<T> getOpener(Class<T> clazz) {
		for (Entry e : list) if (e.clazz == clazz) return e.opener;
		return null;
	}

	public static <T> void register(Domain domain, Class<T> type, GuiOpener<T> opener) {
		list.add(new Entry(type, opener, domain));
	}

	private static class Entry<T> {

		final Class<T> clazz;
		final GuiOpener<T> opener;
		final Domain domain;

		Entry(Class<T> clazz, GuiOpener<T> opener, Domain domain) {
			this.clazz = clazz;
			this.opener = opener;
			this.domain = domain;
		}

	}


}

