package vanilla.worldedit;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class VCommandSet extends CommandBase {
    @Override
    public String getCommandName() {
        return "set";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "гагага";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        BlockPos pos = sender.getCommandSenderEntity().getPosition();
        System.out.println("втф лол");
        new Selection(pos.add(-1, -1, -1), pos.add(1,1, 1)).set(sender.getEntityWorld(), Blocks.cobblestone.getDefaultState());
    }
}
