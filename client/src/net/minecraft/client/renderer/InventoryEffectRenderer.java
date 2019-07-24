package net.minecraft.client.renderer;

import net.minecraft.client.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.element.Colors;
import net.minecraft.client.gui.font.AssetsFontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.Lang;
import net.minecraft.client.settings.Settings;
import net.minecraft.inventory.Container;
import net.minecraft.item.potion.Potion;
import net.minecraft.item.potion.PotionEffect;
import net.minecraft.util.StringUtils;

import java.util.Collection;

public abstract class InventoryEffectRenderer extends GuiContainer {

	/**
	 * True if there is some potion effect to display
	 */
	private boolean hasActivePotionEffects;

	public InventoryEffectRenderer(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	/**
	 * Display the potion effects list
	 */
	public static void drawActivePotionEffects(Gui screen, Minecraft mc, AssetsFontRenderer renderer) {
		Collection<PotionEffect> collection = mc.thePlayer.getActivePotionEffects();

		if (collection.isEmpty()) return;
		//		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		//		GlStateManager.disableLighting();

		if (Settings.MODERN_INVENTORIES.b()) {
			drawPEnew(screen, mc, renderer, collection);
			return;
		}

		int i = 0;
		int j = 0;
		int l = 33;

		if (collection.size() > 5) {
			l = 132 / (collection.size() - 1);
		}

		for (PotionEffect potioneffect : mc.thePlayer.getActivePotionEffects()) {
			Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
			G.color(1.0F, 1.0F, 1.0F, 1.0F);
			mc.getTextureManager().bindTexture(inventoryBackground);
			screen.drawTexturedModalRect(i, j, 0, 166, 140, 32);

			if (potion.hasStatusIcon()) {
				int i1 = potion.getStatusIconIndex();
				screen.drawTexturedModalRect(i + 6, j + 7, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
			}

			String amplifier = "";
			if (potioneffect.getAmplifier() > 0) amplifier = ' ' + StringUtils.romanianNotation(potioneffect.getAmplifier() + 1);
			String s1 = Lang.format(potion.getName()) + amplifier;

			G.disableLighting();
			G.color(1.0F, 1.0F, 1.0F, 1.0F);
			renderer.drawStringWithShadow(s1, (float) (i + 10 + 18), (float) (j + 6), 0xffffff);
			String s = Potion.getDurationString(potioneffect);
			renderer.drawStringWithShadow(s, (float) (i + 10 + 18), (float) (j + 6 + 10), 8355711);
			j += l;
		}
	}

	private static void drawPEnew(Gui screen, Minecraft mc, AssetsFontRenderer fontRendererObj, Collection<PotionEffect> effects) {
		RenderHelper.enableGUIStandardItemLighting();

		int y = 5;

		G.disableLighting();
		G.disableBlend();

		for (PotionEffect e : effects) {
			Potion potion = Potion.potionTypes[e.getPotionID()];
			drawRect(0, y, 80, y + 32, Colors.DARK);
			drawRect(80, y, 82, y + 32, Colors.YELLOW);
			G.color(1.0F, 1.0F, 1.0F);

			if (potion.hasStatusIcon()) {
				int i1 = potion.getStatusIconIndex();
				mc.getTextureManager().bindTexture(inventoryBackground);
				screen.drawTexturedModalRect(4, y + 2, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
			}

			String amplifier = "";
			if (e.getAmplifier() > 0) amplifier = ' ' + StringUtils.romanianNotation(e.getAmplifier() + 1);
			String s1 = Lang.format(potion.getName()) + amplifier;

			//			this.fontRenderer.drawStringWithShadow(s1, (float) (i + 10 + 18), (float) (j + 6), 16777215);
			String s = Potion.getDurationString(e);
			G.color(1.0F, 1.0F, 1.0F, 1.0F);
			fontRendererObj.drawString(s1, 4, y + 20, Colors.WHITE);
			G.scale(2, 2, 2);
			fontRendererObj.drawString(s, 16, y / 2 + 1, Colors.WHITE);
			G.scale(0.5, 0.5, 0.5);
			y += 27 + 10;
		}

		RenderHelper.disableStandardItemLighting();
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	public void initGui() {
		super.initGui();
		this.updateActivePotionEffects();
	}

	protected void updateActivePotionEffects() {

		hasActivePotionEffects = !MC.getPlayer().getActivePotionEffects().isEmpty();

		if (hasActivePotionEffects && !Settings.MODERN_INVENTORIES.b()) this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
		else this.guiLeft = (this.width - this.xSize) / 2;
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (this.hasActivePotionEffects) drawActivePotionEffects(this, mc, fontRendererObj);

	}

}
