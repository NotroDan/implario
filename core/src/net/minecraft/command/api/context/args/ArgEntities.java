package net.minecraft.command.api.context.args;

import net.minecraft.command.api.context.ArgsParser;
import net.minecraft.command.legacy.PlayerSelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.MPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArgEntities<CaptureType extends Entity> extends AbstractArg<Collection<CaptureType>> {

	private final Class<CaptureType> capture;

	public ArgEntities(String caption, String description, Class<CaptureType> capture) {
		super(caption, description);
		this.capture = capture;
	}

	@Override
	public Collection<CaptureType> get(ArgsParser parser) {
		String input = parser.next();
		if (input.startsWith("@")) {
			List<CaptureType> captured = PlayerSelector.matchEntities(parser.getSender(), input, capture);
			if (captured == null || captured.isEmpty()) {
				parser.error("§7Сущностей по селектору §f" + input + "§7 не найдено.");
				return null;
			}
			return captured;
		}
		// ToDo: Взятие сущности по UUID
		parser.error("§4! §cНеверный формат указателя на сущность");
		return null;
	}

}
