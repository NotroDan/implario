package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;

import java.util.List;

public class ServerSelectionList extends GuiListExtended {

	private final GuiMultiplayer owner;
	private final List<ServerListEntryNormal> servers = Lists.newArrayList();
	private final List<ServerListEntryLanDetected> lan = Lists.newArrayList();
	private final GuiListExtended.IGuiListEntry lanScanEntry = new ServerListEntryLanScan();
	private int selectedSlotIndex = -1;

	public ServerSelectionList(GuiMultiplayer ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
		super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
		this.owner = ownerIn;
	}

	/**
	 * Gets the IGuiListEntry object for the given index
	 */
	public GuiListExtended.IGuiListEntry getListEntry(int index) {
		if (index < this.servers.size()) return this.servers.get(index);
		index = index - this.servers.size();

		if (index == 0) return this.lanScanEntry;
		--index;
		return this.lan.get(index);
	}

	protected int getSize() {
		return this.servers.size() + 1 + this.lan.size();
	}

	public void setSelectedSlotIndex(int selectedSlotIndexIn) {
		this.selectedSlotIndex = selectedSlotIndexIn;
	}

	/**
	 * Returns true if the element passed in is currently selected
	 */
	protected boolean isSelected(int slotIndex) {
		return slotIndex == this.selectedSlotIndex;
	}

	public int getSelected() {
		return this.selectedSlotIndex;
	}

	public void copy(ServerList list) {
		this.servers.clear();
		for (int i = 0; i < list.countServers(); ++i)
			this.servers.add(new ServerListEntryNormal(this.owner, list.getServerData(i)));
	}

	public void copyLan(List<LanServerDetector.LanServer> list) {
		this.lan.clear();
		for (LanServerDetector.LanServer lanserverdetector$lanserver : list)
			this.lan.add(new ServerListEntryLanDetected(this.owner, lanserverdetector$lanserver));
	}

	protected int getScrollBarX() {
		return super.getScrollBarX() + 30;
	}

	/**
	 * Gets the width of the list
	 */
	public int getListWidth() {
		return super.getListWidth() + 85;
	}

}
