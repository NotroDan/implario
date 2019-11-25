package net.minecraft.command;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.Player;
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
        IBlockState st = ((ItemBlock)((Player)sender).inventory.getCurrentItem().getItem()).getBlock().getDefaultState();
        Block block = st.getBlock();
        IBlockState states[] = new IBlockState[100000];
        long start = System.nanoTime();
        for(int i = 0; i < 100000; i++)
        states[i] = Block.getStateById(Block.getStateId(st));
        long end = System.nanoTime();
        sender.sendMessage(end - start + "ns blockbyid " + states[125] + " cycle " + (end - start) / 100000);

        start = System.nanoTime();
        for(int i = 0; i < 100000; i++){
            int F = (block.getId() << 4) | block.getMetaFromState(st);
            states[i] = Block.getBlockById(F >> 4).getStateFromMeta(F & 0xF);
        }
        end = System.nanoTime();
        sender.sendMessage(end - start + "ns matematical " + states[(int)(Math.random() * states.length)] + " cycle " + (end - start) / 100000);
    }
}
