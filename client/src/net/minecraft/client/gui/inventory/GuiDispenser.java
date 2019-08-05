package net.minecraft.client.gui.inventory;

import net.minecraft.client.gui.element.Colors;
import net.minecraft.client.gui.element.RenderRec;
import net.minecraft.client.renderer.G;
import net.minecraft.client.settings.Settings;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiDispenser extends GuiContainer {

	private static final ResourceLocation dispenserGuiTextures = new ResourceLocation("textures/gui/container/dispenser.png");

	/**
	 * The player inventory bound to this GUI.
	 */
	private final InventoryPlayer playerInventory;

	/**
	 * The inventory contained within the corresponding Dispenser.
	 */
	public IInventory dispenserInventory;

	public GuiDispenser(InventoryPlayer playerInv, IInventory dispenserInv) {
		super(new ContainerDispenser(playerInv, dispenserInv));
		this.playerInventory = playerInv;
		this.dispenserInventory = dispenserInv;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
	 */
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = this.dispenserInventory.getDisplayName().getUnformattedText();
		this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
	}

	/**
	 * Args : renderPartialTicks, mouseX, mouseY
	 */
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		if (Settings.MODERN_INVENTORIES.b()) {
			int x = guiLeft + 4;
			int y = guiTop + 4;
			RenderRec.render(x, y, 168, 158, Colors.DARK);
			if(Settings.SLOT_GRID.i() != 2) {
				RenderRec.render(x + 2, y + 78, 164, 56, Colors.DARK_GRAY);
				RenderRec.render(x + 2, y + 136, 164, 20, Colors.DARK_GRAY);
				RenderRec.render(x + 56, y + 11, 56, 56, Colors.DARK_GRAY);
			}
		} else {
			G.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(dispenserGuiTextures);
			int i = (this.width - this.xSize) / 2;
			int j = (this.height - this.ySize) / 2;
			this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		}
	}

}
