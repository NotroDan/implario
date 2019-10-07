package net.minecraft.resources.mapping;

import net.minecraft.entity.player.PlayerGuiBridge;

public class MappingIngameGui<T> extends AbstractMapping<PlayerGuiBridge.GuiOpener<T>> {
	private final Class<T> type;

	public MappingIngameGui(Class<T> type, PlayerGuiBridge.GuiOpener overridden, PlayerGuiBridge.GuiOpener actual) {
		super(type.getName(), overridden, actual);
		this.type = type;
	}

	@Override
	public void map(PlayerGuiBridge.GuiOpener opener) {
		PlayerGuiBridge.unregister(type);
		if (opener != null) PlayerGuiBridge.register(type, opener);
	}

}
