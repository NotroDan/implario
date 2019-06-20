package net.minecraft.resources;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.PlayerGuiBridge;
import net.minecraft.item.Item;
import net.minecraft.logging.Log;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.resources.event.E;
import net.minecraft.resources.event.Event;
import net.minecraft.resources.event.Handler;
import net.minecraft.resources.event.PacketInterceptor;
import net.minecraft.resources.override.Mapping;
import net.minecraft.resources.override.MappingBlock;
import net.minecraft.resources.override.MappingItem;
import net.minecraft.resources.override.MappingLambda;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class Registrar {

	private final Domain domain;

	private final List<Mapping> overridden = new ArrayList<>();
	private List<Item> items = new ArrayList<>();
	private List<Block> blocks = new ArrayList<>();
	private List<Class<? extends TileEntity>> tileEntities = new ArrayList<>();
	private List<Class<? extends Entity>> entities = new ArrayList<>();

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
		for (Item item : items) Item.itemRegistry.remove(Item.itemRegistry.getNameForObject(item));
		for (Block block : blocks) Block.blockRegistry.remove(Block.blockRegistry.getNameForObject(block));
		for (Class<? extends TileEntity> tileEntity : tileEntities) TileEntity.unregister(tileEntity);
		for (Mapping entry : overridden) entry.undo();
		for (Class<? extends Entity> entity : entities) EntityList.removeMapping(entity);
	}

	public void registerItem(int id, String textual, Item item) {
		items.add(item);
		Item.registerItem(id, textual, item);
	}

	public void registerBlock(int id, String address, Block block) {
		blocks.add(block);
		Block.registerBlock(id, address, block);
	}

	public void overrideBlock(int id, String address, Block block) {
		Block old = Block.blockRegistry.getObjectById(id);
		if (old != null) override(new MappingBlock(id, address, old, block));
		else {
			Log.MAIN.error("Блок с id " + id + " не зарегистрирован: замещать нечего.");
			registerBlock(id, address, block);
		}
	}

	public void override(Mapping entry) {
		entry.map();
		overridden.add(entry);
	}

	public void overrideItem(int id, String address, Item item) {
		Item old = Item.itemRegistry.getObjectById(id);
		if (old != null) override(new MappingItem(id, address, old, item));
		else {
			Log.MAIN.error("Предмет с id " + id + " не зарегистрирован: замещать нечего.");
			registerItem(id, address, item);
		}
	}

	public <T> void regGui(Class<T> type, PlayerGuiBridge.GuiOpener<T> opener) {
		PlayerGuiBridge.register(domain, type, opener);
	}


	public void registerItemBlock(Block block) {
		Item.registerItemBlock(block);
	}

	public void setWorldServiceProvider(Function<MinecraftServer, WorldService> function) {
		override(new MappingLambda<>(0, "provider", MinecraftServer.WORLD_SERVICE_PROVIDER, function,
				(id, address, element) -> MinecraftServer.WORLD_SERVICE_PROVIDER = element));
	}

	public <T extends TileEntity> void registerTileEntity(Class<T> c, String address) {
		TileEntity.register(c, address);
		tileEntities.add(c);
	}

	public void registerEntity(Class<? extends Entity> type, String address, int id) {
		registerEntity(type, address, id, -2, -2);

	}

	public void registerEntity(Class<? extends Entity> type, String address, int id, int baseColor, int stripColor) {
		EntityList.addMapping(type, address, id, baseColor, stripColor);
		entities.add(type);

	}

	public Collection<Class<? extends Entity>> getEntities() {
		return entities;
	}

}
