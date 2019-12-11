package net.minecraft.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.Player;
import net.minecraft.inventory.CreativeTabs;

import java.util.List;

public class ItemDye extends Item {

	public static final int[] dyeColors = new int[] {
			1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320
	};

	public ItemDye() {
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CreativeTabs.tabMaterials);
	}

	/**
	 * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
	 * different names based on their damage or NBT.
	 */
	public String getUnlocalizedName(ItemStack stack) {
		int i = stack.getMetadata();
		return super.getUnlocalizedName() + "." + EnumDyeColor.byDyeDamage(i).getUnlocalizedName();
	}

	/**
	 * Returns true if the item can be used on the given entity, e.g. shears on sheep.
	 */
	public boolean itemInteractionForEntity(ItemStack stack, Player playerIn, EntityLivingBase target) {
		return false;
	}

	/**
	 * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
	 */
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		for (int i = 0; i < 16; ++i) {
			subItems.add(new ItemStack(itemIn, 1, i));
		}
	}

}
