package net.minecraft.client.gui.ingame;

import net.minecraft.client.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.Settings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ModuleHotbar implements Module {
    @Override
    public void render(GuiIngame gui, float partialTicks, ScaledResolution res) {
        Minecraft mc = MC.i();
        if (mc.playerController.isSpectator()) gui.spectatorGui.renderTooltip(res, partialTicks);
        else renderHotbar(mc, gui, partialTicks, res);
    }

    private void renderHotbar(Minecraft mc, GuiIngame gui, float partialTicks, ScaledResolution res){
        EntityPlayer entityplayer = (EntityPlayer) mc.getRenderViewEntity();
        if (!(mc.getRenderViewEntity() instanceof EntityPlayer)) return;
        int i = res.getScaledWidth() / 2;
        float f = gui.zLevel;
        gui.zLevel = -90.0F;
        if(Settings.MODERN_INVENTORIES.b()){
            Gui.drawRect(i - 91, res.getScaledHeight() - 22, i + 91,  res.getScaledHeight(), 0xFF202020);
            for(int j = 0; j < 9; ++j){
                int x = (res.getScaledWidth() >> 1) - 89 + j * 20;
                int y = res.getScaledHeight() - 20;
                Gui.drawRect(x, y, x + 18, y + 18, 0xFF303030);
            }
            int left = i - 89 + entityplayer.inventory.currentItem * 20;
            Gui.drawRect(left, res.getScaledHeight() - 20, left + 18, res.getScaledHeight() - 2, 0xFF404040);
        }else {
            G.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(GuiIngame.widgetsTexPath);
            gui.drawTexturedModalRect(i - 91, res.getScaledHeight() - 22, 0, 0, 182, 22);
            gui.drawTexturedModalRect(i - 91 - 1 + entityplayer.inventory.currentItem * 20, res.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
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

    private void renderHotbarItem(GuiIngame gui, Minecraft mc, int index, int xPos, int yPos, float partialTicks, EntityPlayer p_175184_5_) {
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
}
