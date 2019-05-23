package vanilla.client.renderer.entity.vanilla;

import net.minecraft.client.game.model.ModelSnowMan;
import vanilla.client.renderer.entity.RenderVanilla;
import net.minecraft.client.renderer.entity.RenderManager;
import vanilla.client.renderer.entity.layers.LayerSnowmanHead;
import vanilla.entity.monster.EntitySnowman;
import net.minecraft.util.ResourceLocation;

public class RenderSnowMan extends RenderVanilla<EntitySnowman>
{
    private static final ResourceLocation snowManTextures = new ResourceLocation("textures/entity/snowman.png");

    public RenderSnowMan(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelSnowMan(), 0.5F);
        this.addLayer(new LayerSnowmanHead(this));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntitySnowman entity)
    {
        return snowManTextures;
    }

    public ModelSnowMan getMainModel()
    {
        return (ModelSnowMan)super.getMainModel();
    }
}
