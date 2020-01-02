package net.minecraft.command;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.Player;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;

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
