package net.minecraft.resources;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerGuiBridge;
import net.minecraft.item.Item;
import net.minecraft.logging.Log;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.resources.event.E;
import net.minecraft.resources.event.Event;
import net.minecraft.resources.event.Handler;
import net.minecraft.resources.event.PacketInterceptor;

import java.util.ArrayList;
import java.util.List;

public class Registrar {

	private final Domain domain;
	private final List<OverridenEntry<Block>> overridenBlocks = new ArrayList<>();
	private final List<OverridenEntry<Item>>  overridenItems =  new ArrayList<>();

	public Registrar(Domain domain) {
		this.domain = domain;
	}

	public Domain getDomain() {
		return domain;
	}

	public <T extends Event> void regListener(Class<T> c, Handler<Event, T> listener) {
		E.getEventLib().registerListener(domain, c, listener);
	}
	public <L extends INetHandler, T extends Packet<L>> void regInterceptor(Class<T> c, PacketInterceptor<L, T> listener) {
		E.getPacketLib().registerListener(domain, c, listener);
	}

	public void unregister() {
		E.getEventLib().LIB.disable(domain);
		E.getPacketLib().LIB.disable(domain);
		for (OverridenEntry<Block> entry : overridenBlocks)
			Block.registerBlock(entry.id, entry.address, entry.old);
		for (OverridenEntry<Item> entry : overridenItems)
			Item.registerItem(entry.id, entry.address, entry.old);
	}

	public void registerItem(int id, String textual, Item item) {
		// ToDo: intercept
		Item.registerItem(id, textual, item);
	}

	public void registerBlock(int id, String address, Block block) {
		Block.registerBlock(id, address, block);
	}

	public void overrideBlock(int id, String address, Block block) {
		Block old = Block.blockRegistry.getObjectById(id);
		if (old == null) {
			Log.MAIN.error("Блок с id " + id + " не зарегистрирован: замещать нечего.");
			return;
		}
		OverridenEntry<Block> e = new OverridenEntry<>(id, address, old, block);
		Block.registerBlock(id, address, block);
		overridenBlocks.add(e);
	}

	public void overrideItem(int id, String address, Item item) {
		Item old = Item.itemRegistry.getObjectById(id);
		if (old == null) {
			Log.MAIN.error("Предмет с id " + id + " не зарегистрирован: замещать нечего.");
			return;
		}
		OverridenEntry<Item> e = new OverridenEntry<>(id, address, old, item);
		Item.registerItem(id, address, item);
		overridenItems.add(e);
	}

	public <T> void regGui(Class<T> type, PlayerGuiBridge.GuiOpener<T> opener) {
		PlayerGuiBridge.register(domain, type, opener);
	}

	public <T> void regRenderer(Class<T> c, Render<T> render) {
	}

}
