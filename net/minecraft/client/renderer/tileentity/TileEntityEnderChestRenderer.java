package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.G;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.ResourceLocation;

public class TileEntityEnderChestRenderer extends TileEntitySpecialRenderer<TileEntityEnderChest>
{
    private static final ResourceLocation ENDER_CHEST_TEXTURE = new ResourceLocation("textures/entity/chest/ender.png");
    private ModelChest field_147521_c = new ModelChest();

    public void renderTileEntityAt(TileEntityEnderChest te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        int i = 0;

        if (te.hasWorldObj())
        {
            i = te.getBlockMetadata();
        }

        if (destroyStage >= 0)
        {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            G.matrixMode(5890);
            G.pushMatrix();
            G.scale(4.0F, 4.0F, 1.0F);
            G.translate(0.0625F, 0.0625F, 0.0625F);
            G.matrixMode(5888);
        }
        else
        {
            this.bindTexture(ENDER_CHEST_TEXTURE);
        }

        G.pushMatrix();
        G.enableRescaleNormal();
        G.color(1.0F, 1.0F, 1.0F, 1.0F);
        G.translate((float)x, (float)y + 1.0F, (float)z + 1.0F);
        G.scale(1.0F, -1.0F, -1.0F);
        G.translate(0.5F, 0.5F, 0.5F);
        int j = 0;

        if (i == 2)
        {
            j = 180;
        }

        if (i == 3)
        {
            j = 0;
        }

        if (i == 4)
        {
            j = 90;
        }

        if (i == 5)
        {
            j = -90;
        }

        G.rotate((float)j, 0.0F, 1.0F, 0.0F);
        G.translate(-0.5F, -0.5F, -0.5F);
        float f = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;
        f = 1.0F - f;
        f = 1.0F - f * f * f;
        this.field_147521_c.chestLid.rotateAngleX = -(f * (float)Math.PI / 2.0F);
        this.field_147521_c.renderAll();
        G.disableRescaleNormal();
        G.popMatrix();
        G.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (destroyStage >= 0)
        {
            G.matrixMode(5890);
            G.popMatrix();
            G.matrixMode(5888);
        }
    }
}
