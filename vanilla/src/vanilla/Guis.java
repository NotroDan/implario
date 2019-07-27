package vanilla;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.util.IChatComponent;
import vanilla.entity.IMerchant;
import vanilla.entity.passive.EntityHorse;
import vanilla.inventory.ContainerHorseInventory;
import vanilla.inventory.ContainerMerchant;
import vanilla.world.gen.feature.village.MerchantRecipeList;

public class Guis {

	public static void displayVillagerTradeGui(MPlayer player, IMerchant villager) {
		player.getNextWindowId();
		player.openContainer = new ContainerMerchant(player.inventory, villager, player.worldObj);
		player.openContainer.windowId = player.currentWindowId;
		player.openContainer.onCraftGuiOpened(player);
		IInventory iinventory = ((ContainerMerchant) player.openContainer).getMerchantInventory();
		IChatComponent ichatcomponent = villager.getDisplayName();
		player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, "minecraft:villager", ichatcomponent, iinventory.getSizeInventory()));
		MerchantRecipeList merchantrecipelist = villager.getRecipes(player);

		if (merchantrecipelist != null) {
			PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
			packetbuffer.writeInt(player.currentWindowId);
			merchantrecipelist.writeToBuf(packetbuffer);
			player.playerNetServerHandler.sendPacket(new S3FPacketCustomPayload("MC|TrList", packetbuffer));
		}
	}

	public static void displayGUIHorse(MPlayer player, EntityHorse horse, IInventory horseInventory) {
		if (player.openContainer != player.inventoryContainer) player.closeScreen();

		player.getNextWindowId();
		player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(
				player.currentWindowId, "EntityHorse", horseInventory.getDisplayName(), horseInventory.getSizeInventory(), horse.getEntityId()));
		player.openContainer = new ContainerHorseInventory(player.inventory, horseInventory, horse, player);
		player.openContainer.windowId = player.currentWindowId;
		player.openContainer.onCraftGuiOpened(player);
	}

}
