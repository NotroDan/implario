package net.minecraft.client.renderer.entity.vanilla;

import net.minecraft.client.game.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerSheepWool;
import vanilla.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;

public class RenderSheep extends RenderLiving<EntitySheep>
{
    private static final ResourceLocation shearedSheepTextures = new ResourceLocation("textures/entity/sheep/sheep.png");

    public RenderSheep(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn)
    {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
        this.addLayer(new LayerSheepWool(this));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntitySheep entity)
    {
        return shearedSheepTextures;
    }
}
