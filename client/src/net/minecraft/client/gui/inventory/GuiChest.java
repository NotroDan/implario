package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.element.Colors;
import net.minecraft.client.renderer.G;
import net.minecraft.client.settings.Settings;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.ResourceLocation;

public class GuiChest extends GuiContainer {

	/**
	 * The ResourceLocation containing the chest GUI texture.
	 */
	private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	private Inventory upperChestInventory;
	private Inventory lowerChestInventory;

	/**
	 * window height is calculated with these values; the more rows, the heigher
	 */
	private int inventoryRows;

	public GuiChest(Inventory upperInv, Inventory lowerInv) {
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
		this.fontRendererObj.drawString(this.lowerChestInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
		this.fontRendererObj.drawString(this.upperChestInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
	}

	/**
	 * Args : renderPartialTicks, mouseX, mouseY
	 */
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		int x = guiLeft;
		int y = guiTop;

		if (Settings.MODERN_INVENTORIES.b()) {
			int s = Settings.SLOT_GRID.i();
			int y0 = y + 16;
			drawRect(x + 4, y + 4, x + xSize - 4, (y += inventoryRows * 18 + 17) + 92, Colors.DARK);
			if (s != 2) drawRect(x + 6, y0, x + xSize - 6, y + 1, Colors.DARK_GRAY);

			//drawRect(x, y, x + xSize, y += 2, 0xe0ffa114);

			if (s != 2) drawRect(x + 6, y + 12, x + xSize - 6, y + 68, Colors.DARK_GRAY);
			y += 66;
			if (s != 2) drawRect(x + 6, y + 4, x + xSize - 6, y + 24, Colors.DARK_GRAY);
		} else {
			this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
			this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
			this.drawTexturedModalRect(x, y + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
		}
	}

}
