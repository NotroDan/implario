package net.minecraft.client.gui.inventory;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.element.Colors;
import net.minecraft.client.gui.element.RenderRec;
import net.minecraft.client.renderer.G;
import net.minecraft.client.resources.Lang;
import net.minecraft.client.settings.Settings;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GuiCrafting extends GuiContainer
{
    private static final ResourceLocation craftingTableGuiTextures = new ResourceLocation("textures/gui/container/crafting_table.png");

    public GuiCrafting(InventoryPlayer playerInv, World worldIn)
    {
        this(playerInv, worldIn, BlockPos.ORIGIN);
    }

    public GuiCrafting(InventoryPlayer playerInv, World worldIn, BlockPos blockPosition){
        super(new ContainerWorkbench(playerInv, worldIn, blockPosition));
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString(Lang.format("container.crafting", new Object[0]), 28, 6, 4210752);
        this.fontRendererObj.drawString(Lang.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if(Settings.MODERN_INVENTORIES.b()){
            int x = guiLeft + 4;
            int y = guiTop + 2;
            RenderRec.render(x, y, 168, 160, Colors.DARK);

            if(Settings.SLOT_GRID.i() != 2) {
                RenderRec.render(x + 2, y + 80, 164, 56, Colors.DARK_GRAY);
                RenderRec.render(x + 2, y + 138, 164, 20, Colors.DARK_GRAY);
                RenderRec.render(x + 24, y + 13, 56, 56, Colors.DARK_GRAY);
                RenderRec.render(x + 118, y + 31, 20, 20, Colors.DARK_GRAY);
            }

            RenderRec.render(x + 88, y + 39, 16, 4, Colors.GRAY);
            int x1 = x + 104, y1 = y + 33;
            Gui.drawTriangle(x1, y1, x1, y1 + 16, x1 + 8, y1 + 8, Colors.GRAY);
        }else {
            G.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(craftingTableGuiTextures);

            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            this.drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
        }
    }
}
