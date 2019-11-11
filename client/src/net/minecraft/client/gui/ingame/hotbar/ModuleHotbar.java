package net.minecraft.client.gui.ingame.hotbar;

import net.minecraft.client.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.element.Colors;
import net.minecraft.client.gui.element.RenderElement;
import net.minecraft.client.gui.element.RenderRec;
import net.minecraft.client.gui.element.RenderRunnable;
import net.minecraft.client.gui.ingame.GuiIngame;
import net.minecraft.client.gui.ingame.Module;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.Settings;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ModuleHotbar implements Module {

	private final List<RenderElement> render = new ArrayList<>(10);
	private boolean lastValue = false;
	private int lastSize = 0;

	@Override
	public void render(GuiIngame gui, float partialTicks, ScaledResolution res) {
		Minecraft mc = MC.i();
		if (mc.playerController.isSpectator()) gui.spectatorGui.renderTooltip(res, partialTicks);
		else renderHotbar(mc, gui, partialTicks, res);
	}

	private void renderHotbar(Minecraft mc, GuiIngame gui, float partialTicks, ScaledResolution res) {
		Player entityplayer = (Player) mc.getRenderViewEntity();
		if (!(mc.getRenderViewEntity() instanceof Player)) return;
		int i = res.getScaledWidth() >> 1;
		boolean settings = Settings.MODERN_INVENTORIES.b();
		if (settings != lastValue || lastSize != res.getScaledHeight()) generateRender(mc, gui, res, i);
		float f = gui.zLevel;
		gui.zLevel = -90.0F;
		RenderElement.render(render);
		int currentItem = entityplayer.inventory.getCurrentSlot();
		if (settings) {
			RenderRec.render(i - 90 + currentItem * 20,
					res.getScaledHeight() - 21, 20, 20, Colors.YELLOW);
			RenderRec.render(i - 89 + currentItem * 20,
					res.getScaledHeight() - 20, 18, 18, Colors.LIGHT);
		} else {
			gui.drawTexturedModalRect(i - 91 - 1 + currentItem * 20,
					res.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
		}
		gui.zLevel = f;
		G.enableRescaleNormal();
		G.enableBlend();
		G.tryBlendFuncSeparate(770, 771, 1, 0);

		RenderHelper.enableGUIStandardItemLighting();

		for (int j = 0; j < 9; ++j) {
			int x = (res.getScaledWidth() >> 1) - 90 + j * 20 + 2;
			int y = res.getScaledHeight() - 16 - 3;
			renderHotbarItem(gui, mc, j, x, y, partialTicks, entityplayer);
		}

		RenderHelper.disableStandardItemLighting();
		G.disableRescaleNormal();
		G.disableBlend();
	}

	private void renderHotbarItem(GuiIngame gui, Minecraft mc, int index, int xPos, int yPos, float partialTicks, Player p_175184_5_) {
		ItemStack itemstack = p_175184_5_.inventory.mainInventory[index];

		if (itemstack != null) {
			float f = (float) itemstack.animationsToGo - partialTicks;

			if (f > 0.0F) {
				G.pushMatrix();
				float f1 = 1.0F + f / 5.0F;
				G.translate((float) (xPos + 8), (float) (yPos + 12), 0.0F);
				G.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
				G.translate((float) -(xPos + 8), (float) -(yPos + 12), 0.0F);
			}

			gui.itemRenderer.renderItemAndEffectIntoGUI(itemstack, xPos, yPos);

			if (f > 0.0F) {
				G.popMatrix();
			}

			gui.itemRenderer.renderItemOverlays(mc.fontRenderer, itemstack, xPos, yPos);
		}
	}

	private void generateRender(Minecraft mc, GuiIngame gui, ScaledResolution res, int i) {
		render.clear();
		lastSize = res.getScaledHeight();
		lastValue = Settings.MODERN_INVENTORIES.b();
		if (lastValue) {
			render.add(new RenderRec(i - 91, res.getScaledHeight() - 22, 182, 22, Colors.DARK));
			for (int j = 0; j < 9; ++j) {
				int x = (res.getScaledWidth() >> 1) - 89 + j * 20;
				int y = res.getScaledHeight() - 20;
				render.add(new RenderRec(x, y, 18, 18, Colors.GRAY));
			}
		} else {
			render.add(new RenderRunnable(() -> {
				G.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(GuiIngame.widgetsTexPath);
				gui.drawTexturedModalRect(i - 91, res.getScaledHeight() - 22, 0, 0, 182, 22);
			}));
		}
	}

}
