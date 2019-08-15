package net.minecraft.resources;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.PlayerGuiBridge;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.resources.event.E;
import net.minecraft.resources.event.Event;
import net.minecraft.resources.event.Handler;
import net.minecraft.resources.event.PacketInterceptor;
import net.minecraft.resources.mapping.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldService;
import net.minecraft.world.WorldType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class Registrar {

	private final Domain domain;

	/**
	 * Маппинги используются для удобной замены существующих вещей.
	 * В маппингах есть метод для регистрации и удаления вещей из памяти.
	 */
	private final List<Mapping> mappings = new ArrayList<>();

	public Registrar(Domain domain) {
		this.domain = domain;
	}

	public Domain getDomain() {
		return domain;
	}


	protected void unregister() {
		E.getEventLib().LIB.disable(domain);
		E.getPacketLib().LIB.disable(domain);
		Collections.reverse(mappings);
		for (Mapping entry : mappings) entry.revert();
		mappings.clear();
	}

	/**
	 * Регистрация слушателя событий.
	 * События это сомнительная штука, и ими лучше не пользоваться.
	 * События не поддерживают наследование!
	 *
	 * @param c        Класс события
	 * @param listener Слушатель этого типа событий (Наследуйтесь от Handler<Event, T>)
	 * @param <T>      Тип события
	 */
	public <T extends Event> void regListener(Class<T> c, Handler<Event, T> listener) {
		E.getEventLib().registerListener(domain, c, listener);
	}

	/**
	 * @param command гагагаг
	 * @param <T>     гагага
	 */
	public <T extends CommandBase> void regCommand(T command) {
		registerMapping(new MappingCommand(CommandHandler.getCommand(command.getCommandName()), command));
	}

	/**
	 * Перехватчики пакетов.
	 *
	 * @param c        Класс пакета
	 * @param listener Слушатель пакета (Наследуйтесь от PacketInterceptor<L, T>)
	 * @param <L>      Тип хэндлера этого пакета (INetHandlerPlayClient, INetHandlerLoginServer, и т. п.)
	 * @param <T>      Тип пакета
	 */
	public <L extends INetHandler, T extends Packet<L>> void regInterceptor(Class<T> c, PacketInterceptor<L, T> listener) {
		E.getPacketLib().registerListener(domain, c, listener);
	}

	/**
	 * Регистрация предмета, поддерживает замену существующих.
	 * Список существующих итемов и их идентификаторов можно найти в классе net.minecraft.item.Item
	 *
	 * @param id      ID предмета. При замене указать ID существующего
	 * @param textual Текстовый идентификатор. При замене скопировать его у существующего
	 * @param item    Ваш предмет
	 * @see Item
	 */
	public void registerItem(int id, String textual, Item item) {
		registerMapping(new MappingItem(id, textual, Item.itemRegistry.getObjectById(id), item));
	}

	/**
	 * Регистрация блока, поддерживает замену существующих.
	 * Список существующих блоков и их идентификаторов можно найти в классе net.minecraft.block.Block
	 *
	 * @param id      ID блока. При замене указать ID существующего
	 * @param address Текстовый идентификатор. При замене скопировать у существующего
	 * @param block   Ваш блок
	 * @see Block
	 */
	public void registerBlock(int id, String address, Block block) {
		Block old = Block.blockRegistry.getObjectById(id);
		if (old == null || old.getMaterial() == Material.air) old = null;
		registerMapping(new MappingBlock(id, address, old, block));
	}

	/**
	 * Блоки, которые можно взять в руку в виде предмета, нужно регистрировать отдельно.
	 * Почему? Некоторые блоки нельзя брать: эндер-портал, огонь, вода/лава
	 * Поддерживает замену. // ToDo: Криво поддерживает замену!!1
	 */
	public void registerItemBlock(Block block) {
		registerMapping(new MappingItemBlock(block));
	}

	/**
	 * В отличие от предметов и блоков, сервер передаёт клиенту данные о TileEntity,
	 * используя строки (названия), а не цифровые ID.
	 * Поддерживает замену.
	 *
	 * @param c       Класс TileEntity
	 * @param address Буквенный идентификатор, который будет передаваться в NBT.
	 */
	public <T extends TileEntity> void registerTileEntity(Class<T> c, String address) {
		registerMapping(new MappingTileEntity(0, address, TileEntity.getClassForName(address), c));
	}

	/**
	 * WorldService управляет мирами на сервере.
	 * Если вы хотите использовать свои реализации миров, измените WorldService.
	 * Поддерживает замену.
	 *
	 * @param function Функция-поставщик ворлдсервисов для объектов MinecraftServer.
	 */
	public void setWorldServiceProvider(Function<MinecraftServer, WorldService> function) {
		replaceProvider(MinecraftServer.WORLD_SERVICE_PROVIDER, function);
	}

	/**
	 * Замена функции провайдера (Напр. WorldService или MusicType)
	 *
	 * @param function ваша новая функция
	 */
	public <I, O> void replaceProvider(Provider<I, O> provider, Function<I, O> function) {
		registerMapping(new MappingProvider<>(provider, function));
	}

	/**
	 * Внутриигровые гуи (Печки, верстаки, сундуки, варочные стойки)
	 *
	 * @param type   Класс, по которому происходит выбор. Может быть абсолютно любым, но лучше сделать его логичным.
	 * @param opener Функция открыватель. Подробнее:
	 * @see PlayerGuiBridge.GuiOpener
	 */
	public <T> void registerIngameGui(Class<T> type, PlayerGuiBridge.GuiOpener<T> opener) {
		registerMapping(new MappingIngameGui<>(type, PlayerGuiBridge.getOpener(type), opener));
	}

	/**
	 * Кастомные энтити
	 * Поддерживает замену.
	 *
	 * @param type    Класс сущности
	 * @param address Буквенный ID
	 * @param id      Циферный ID (Не советую ставить больше 255)
	 */
	public void registerEntity(Class<? extends Entity> type, String address, int id) {
		registerMapping(new MappingEntity(id, address, EntityList.getClassFromID(id), type));
	}

	/**
	 * Кастомные мобы.
	 * Отличаются от обычных энтити наличием собственных статистик и яиц спавна.
	 * Если вам не нужно яйцо и статистика, используйте registerEntity()
	 * Поддерживает замену.
	 *
	 * @param type      Класс сущности
	 * @param address   Буквенный ID
	 * @param id        Циферный ID
	 * @param baseColor Цвет яйца в формате 0xAARRGGBB (AlphaRedGreenBlue)
	 * @param spotColor Цвет пятнышек на яйце в аналогичном формате
	 */
	public void registerMob(Class<? extends Entity> type, String address, int id, int baseColor, int spotColor) {
		Class<? extends Entity> oldType = EntityList.getClassFromID(id);
		EntityList.EntityEggInfo oldEgg = EntityList.entityEggs.get(id);
		registerMapping(new MappingMob(id, address, oldType, type, oldEgg, baseColor, spotColor));
	}

	/**
	 * Регистрация типа генерации мира.
	 * НЕ ПОДДЕРЖИВАЕТ ЗАМЕНУ.
	 */
	public void registerWorldType(WorldType worldType) {
		registerMapping(new MappingWorldType(worldType));
	}

	/**
	 * Регистрация кастомного маппинга для тех, кто хочет модифицировать датапак из другого датапака.
	 */
	public void registerMapping(Mapping mapping) {
		mapping.apply();
		mappings.add(mapping);
	}


}
