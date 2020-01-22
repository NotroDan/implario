package net.minecraft.item;

import net.minecraft.entity.player.Player;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.protocol.minecraft_47.play.server.S2FPacketSetSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.util.functional.StringUtils;
import net.minecraft.util.chat.ChatComponentProcessor;
import net.minecraft.util.chat.ChatComponentText;
import net.minecraft.world.World;

import java.util.List;

public class ItemEditableBook extends Item {

	public ItemEditableBook() {
		this.setMaxStackSize(1);
	}

	public static boolean validBookTagContents(NBTTagCompound nbt) {
		if (!ItemWritableBook.isNBTValid(nbt)) {
			return false;
		}
		if (!nbt.hasKey("title", 8)) {
			return false;
		}
		String s = nbt.getString("title");
		return s != null && s.length() <= 32 ? nbt.hasKey("author", 8) : false;
	}

	/**
	 * Gets the generation of the book (how many times it has been cloned)
			*/
	public static int getGeneration(ItemStack book) {
		return book.getTagCompound().getInteger("generation");
	}

	public String getItemStackDisplayName(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound nbttagcompound = stack.getTagCompound();
			String s = nbttagcompound.getString("title");

			if (!StringUtils.isNullOrEmpty(s)) {
				return s;
			}
		}

		return super.getItemStackDisplayName(stack);
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	public void addInformation(ItemStack stack, Player playerIn, List<String> tooltip, boolean advanced) {
		if (stack.hasTagCompound()) {
			NBTTagCompound nbttagcompound = stack.getTagCompound();
			String s = nbttagcompound.getString("author");

			if (!StringUtils.isNullOrEmpty(s)) {
				tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("book.byAuthor", new Object[] {s}));
			}

			tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("book.generation." + nbttagcompound.getInteger("generation")));
		}
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	 */
	public ItemStack onItemRightClick(ItemStack stack, World worldIn, Player p) {
		if (!worldIn.isClientSide) this.resolveContents(stack, p);

		p.openGui(ItemStack.class, stack);
		//		p.displayGUIBook(stack);
		p.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
		return stack;
	}

	private void resolveContents(ItemStack stack, Player player) {
		if (stack == null || stack.getTagCompound() == null) return;
		NBTTagCompound nbttagcompound = stack.getTagCompound();

		if (nbttagcompound.getBoolean("resolved")) return;
		nbttagcompound.setBoolean("resolved", true);

		if (!validBookTagContents(nbttagcompound)) return;

		NBTTagList nbttaglist = nbttagcompound.getTagList("pages", 8);

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			String s = nbttaglist.getStringTagAt(i);
			IChatComponent lvt_7_1_;

			try {
				lvt_7_1_ = IChatComponent.Serializer.jsonToComponent(s);
				IChatComponent processed = ChatComponentProcessor.processComponent(player, lvt_7_1_, player);
				if (processed != null) lvt_7_1_ = processed;
			} catch (Exception var9) {
				lvt_7_1_ = new ChatComponentText(s);
			}

			nbttaglist.set(i, new NBTTagString(IChatComponent.Serializer.componentToJson(lvt_7_1_)));
		}

		nbttagcompound.setTag("pages", nbttaglist);

		if (player instanceof MPlayer && player.inventory.getCurrentItem() == stack) {
			Slot slot = player.openContainer.getSlotFromInventory(player.inventory, player.inventory.getCurrentSlot());
			((MPlayer) player).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(0, slot.slotNumber, stack));
		}
	}

	public boolean hasEffect(ItemStack stack) {
		return true;
	}

}
