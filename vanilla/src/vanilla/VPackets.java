package vanilla;

import net.minecraft.inventory.Container;
import net.minecraft.logging.Log;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.resources.Registrar;
import vanilla.inventory.ContainerMerchant;

public class VPackets {

	public void load(Registrar registrar) {

		// Client packets
		registrar.regInterceptor(C17PacketCustomPayload.class, this::handleCustomPayload);

	}

	private boolean handleCustomPayload(C17PacketCustomPayload p, INetHandlerPlayServer l) {
		if ("MC|TrSel".equals(p.getChannelName())) {
			try {
				int i = p.getBufferData().readInt();
				Container container = l.getPlayer().openContainer;

				if (container instanceof ContainerMerchant) {
					((ContainerMerchant) container).setCurrentRecipeIndex(i);
				}
			} catch (Exception e) {
				Log.MAIN.error("Couldn't select trade", e);
			}
			return true;
		}
		return false;
	}


}
