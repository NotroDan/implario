package net.minecraft.resources.event.events.player;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

@Getter
public class PlayerBlockPlaceEvent extends APlayerСancelableEvent{
    private final Block block;
    private final BlockPos pos;

    public PlayerBlockPlaceEvent(Player player, Block block, BlockPos pos){
        super(player);
        this.block = block;
        this.pos = pos;
    }
}
