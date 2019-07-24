package net.minecraft.command;

import net.minecraft.util.chat.ChatComponentText;

public class CommandSet extends CommandBase{
    @Override
    public String getCommandName() {
        return "hello";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.hello.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(new ChatComponentText("Hello"));
    }
}
