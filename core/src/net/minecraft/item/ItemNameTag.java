package net.minecraft.item;

import net.minecraft.inventory.creativetab.CreativeTabs;
import net.minecraft.entity.VanillaEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class ItemNameTag extends Item
{
    public ItemNameTag()
    {
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target)
    {
        if (!stack.hasDisplayName())
        {
            return false;
        }
		if (target instanceof VanillaEntity)
		{
			VanillaEntity entityliving = (VanillaEntity)target;
			entityliving.setCustomNameTag(stack.getDisplayName());
			entityliving.enablePersistence();
			--stack.stackSize;
			return true;
		}
		return super.itemInteractionForEntity(stack, playerIn, target);
	}
}