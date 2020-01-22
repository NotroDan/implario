package net.minecraft.command.impl.core;

import net.minecraft.command.api.ICommandSender;

public class CommandMemory extends CommandBase{
    @Override
    public String getCommandName() {
        return "memory";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        long used = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        long free = Runtime.getRuntime().freeMemory() / 1024 / 1024;
        long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        sender.sendMessage( used - free + "mb used, " + free + "mb free, " + total + "mb total");
    }
}
