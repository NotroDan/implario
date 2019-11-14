package net.minecraft.resources;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.PlayerGuiBridge;
import net.minecraft.item.Item;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.resources.event.*;
import net.minecraft.resources.mapping.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldService;
import net.minecraft.world.WorldType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
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
		E.getPacketLib().LIB.disable(domain);
		Collections.reverse(mappings);
		for (Mapping entry : mappings) entry.revert();
		mappings.clear();
	}

	/**
	 * @param command Команда является типом T
	 * @param <T>     Тип наследуемый от ICommand
	 */
	public <T extends ICommand> void registerCommand(T command) {
		registerMapping(new MappingCommand(command));
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
	 * Поддерживает замену. // ToDo: Криво поддерживает замену!!1 (но ведь вроде работает)
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

	public <T extends Event> void registerListener(EventManager<T> manager, Listener<T> listener){
		registerMapping(new MappingEvent<>(manager, listener));
	}

	/**
	 * @param manager Что то вроде ServerEvents.playerMove
	 * @param listener Тот кто будет слушать
	 * @param priority Ниже, раньше выполнится
	 * @param ignoreCancelled Отменять вызов метода когда был вызван cancel(true)
	 * @param <T> Класс эвента
	 */
	public <T extends Event> void registerListener(EventManager<T> manager, Consumer<T> listener, int priority, boolean ignoreCancelled){
		registerListener(manager, new Listener<T>() {
			@Override
			public void process(T event) {
				listener.accept(event);
			}

			@Override
			public boolean ignoreCancelled() {
				return ignoreCancelled;
			}

			@Override
			public int priority() {
				return priority;
			}
		});
	}

	public <T extends Event> void registerListener(EventManager<T> manager, Consumer<T> listener, int priority){
		registerListener(manager, listener, priority, false);
	}

	public <T extends Event> void registerListener(EventManager<T> manager, Consumer<T> listener, boolean ignoreCancelled){
		registerListener(manager, listener, 0, ignoreCancelled);
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
