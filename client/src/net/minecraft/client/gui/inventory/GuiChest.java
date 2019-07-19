package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.G;
import net.minecraft.client.settings.Settings;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiChest extends GuiContainer {

	/**
	 * The ResourceLocation containing the chest GUI texture.
	 */
	private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	private IInventory upperChestInventory;
	private IInventory lowerChestInventory;

	/**
	 * window height is calculated with these values; the more rows, the heigher
	 */
	private int inventoryRows;

	public GuiChest(IInventory upperInv, IInventory lowerInv) {
		super(new ContainerChest(upperInv, lowerInv, Minecraft.getMinecraft().thePlayer));
		this.upperChestInventory = upperInv;
		this.lowerChestInventory = lowerInv;
		this.allowUserInput = false;
		int i = 222;
		int j = i - 108;
		this.inventoryRows = lowerInv.getSizeInventory() / 9;
		this.ySize = j + this.inventoryRows * 18;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
	 */
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (Settings.MODERN_INVENTORIES.b()) {
			drawCenteredString(fontRendererObj, this.lowerChestInventory.getDisplayName().getUnformattedText(), xSize / 2, 3, 0xd0d0d0);
			drawCenteredString(fontRendererObj, this.upperChestInventory.getDisplayName().getUnformattedText(), xSize / 2, this.ySize - 96 + 2, 0xe0ffa114);
		} else {
			this.fontRendererObj.drawString(this.lowerChestInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
			this.fontRendererObj.drawString(this.upperChestInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
		}
	}

	/**
	 * Args : renderPartialTicks, mouseX, mouseY
	 */
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;

		if (Settings.MODERN_INVENTORIES.b()) {
			int s = Settings.SLOT_GRID.i();
			int y0 = y + 18;
			drawRect(x, y, x + xSize, y += inventoryRows * 18 + 17, 0xe7202020);
			if (s != 2) drawRect(x + 6, y0, x + xSize - 6, y, 0xff232323);

			drawRect(x, y, x + xSize, y += 2, 0xe0ffa114);

			drawRect(x, y, x + xSize, y + 96, 0xe7202020);

			if (s != 2) drawRect(x + 6, y + 10, x + xSize - 6, y + 66, 0xff232323);
			y += 66;
			if (s != 2) drawRect(x + 6, y + 2, x + xSize - 6, y + 22, 0xff232323);
		} else {
			this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
			this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
			this.drawTexturedModalRect(x, y + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
		}
	}

}
