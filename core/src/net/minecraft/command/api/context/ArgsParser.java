package net.minecraft.command.api.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.command.api.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor
public class ArgsParser implements Iterator<String> {

	@Getter
	private final ICommandSender sender;

	@Getter
	private final MinecraftServer server;
	private final ContextCommand command;
	private final String[] argsArray;
	private int currentArg;
	private final Map<Arg, Object> parsed = new HashMap<>();

	@Getter
	private String error;

	public ContextImpl parse() {
		if (argsArray.length < command.getEssentialArgsAmount()) {
			sender.sendMessage("§7/" + command.getAddress() + ": §f" + command.getDescription());
			StringBuilder usageStr = new StringBuilder("§7Использование: §f/").append(command).append(' ');
			int requiredCounter = 0;
			for (Arg arg : command.getArgs()) {
				if (!arg.isEssential()) {
					usageStr.append(arg.getCaption().replace("<", "§8[§f").replace(">", "§8]§f"))
							.append(" ");
					continue;
				}
				String bracketColor = (requiredCounter += arg.getEssentialPartsAmount()) < argsArray.length ? "§7" : "§c§l";
				usageStr.append(arg.getCaption().replace("<", bracketColor + "<§f").replace(">", bracketColor + ">§f"))
						.append(" ");
			}
			sender.sendMessage(usageStr.toString());
			return null;
		}

		int optionalArgsPoolSize = argsArray.length - command.getEssentialArgsAmount();

		for (Arg arg : command.getArgs()) {
			Object value;

			if (!arg.isEssential()) {
				if (optionalArgsPoolSize >= arg.getEssentialPartsAmount()) value = arg.get(this);
				else value = arg.getDefaultValue(this);
			} else value = arg.get(this);

			if (value == null) {
				sender.sendMessage("§cОшибка: " + error);
				return null;
			}
			parsed.put(arg, value);
		}

		return new ContextImpl(server, command, sender, parsed);

	}

	public void error(String message) {
		this.error = message;
	}

	@Override
	public boolean hasNext() {
		return currentArg >= argsArray.length;
	}

	@Override
	public String next() {
		return argsArray[currentArg++];
	}



}
