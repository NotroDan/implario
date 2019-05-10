package net.minecraft.client.renderer.entity.vanilla;

import net.minecraft.client.game.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import vanilla.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;

public class RenderCow extends RenderLiving<EntityCow>
{
    private static final ResourceLocation cowTextures = new ResourceLocation("textures/entity/cow/cow.png");

    public RenderCow(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn)
    {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityCow entity)
    {
        return cowTextures;
    }
}
