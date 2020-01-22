package net.minecraft.command.impl.core;

import net.minecraft.command.api.ICommandSender;
import net.minecraft.inventory.InventoryBasic;

public class CommandGC extends CommandBase{
    @Override
    public String getCommandName() {
        return "gc";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        getCommandSenderAsPlayer(sender).openGui(new InventoryBasic("я ууу", false, 12));
    }
}
