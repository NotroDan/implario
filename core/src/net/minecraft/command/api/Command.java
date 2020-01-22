package net.minecraft.command.api;

import net.minecraft.entity.player.MPlayer;
import net.minecraft.util.BlockPos;

import java.util.Collection;

public interface Command {

	/**
	 * @return Имя команды - отличительная черта от остальных команд
	 */
	String getAddress();

	/**
	 * @return Описание того, что делает команда.
	 */
	String getDescription();

	/**
	 * Лестница прав, к которой принадлежит эта команда.
	 * Например mgr - управляющий (manaager)
	 * @return трёхбуквенный идентификатор лестницы
	 */
	default String getPermissionLadder() {
		return "mgr";
	}

	/**
	 * Необходимый уровень доступа в лестнице getPermissionLadder()
	 * @return число от 1 до 10, 0 если доступ разрешён кому угодно
	 */
	int getPermissionLevel();

	/**
	 * Выполнение команды
	 * @return Количество объектов, затронутых после выполнения команды (напр. убитых сущностей)
	 */
	int execute(ICommandSender sender, String[] args);

	/**
	 * Варианты автоматического дополнения команды при нажатии Tab
	 * @param player Игрок, запросивший автодополнение
	 * @param pos Блок, на который смотрит игрок
	 * @param args Последний элемент массива - начало строки, которое надо дополнить
	 * @return Варианты для замены последнего аргумента
	 */
	Collection<String> tabComplete(MPlayer player, BlockPos pos, String[] args);

}
