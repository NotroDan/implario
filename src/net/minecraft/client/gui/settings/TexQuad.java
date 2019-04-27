package net.minecraft.client.gui.settings;

import net.minecraft.client.MC;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class TexQuad {

	public final float u1, v1, u2, v2;
	public final int width, height;

	public TexQuad(int x, int y, int width, int height, int texW, int texH) {
		u1 = (float) x / (float) texW;
		u2 = ((float) x + (float) width) / (float) texW;
		v1 = (float) y / (float) texH;
		v2 = ((float) y + (float) height) / (float) texH;
		this.width = width;
		this.height = height;
	}

	public void draw(ResourceLocation tex) {
		MC.bindTexture(tex);
		Tessellator t = Tessellator.getInstance();
		WorldRenderer r = t.getWorldRenderer();
		r.begin(7, DefaultVertexFormats.POSITION_TEX);
		r.pos(0, 0, 0).tex(u1, v1).endVertex();
		r.pos(width, 0, 0).tex(u2, v1).endVertex();
		r.pos(width, height, 0).tex(u2, v2).endVertex();
		r.pos(0, height, 0).tex(u1, v2).endVertex();
		t.draw();
	}

}
