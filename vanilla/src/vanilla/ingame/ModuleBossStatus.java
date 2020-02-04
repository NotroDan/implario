package vanilla.ingame;

import net.minecraft.client.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.font.MCFontRenderer;
import net.minecraft.client.gui.ingame.GuiIngame;
import net.minecraft.client.gui.ingame.Module;
import net.minecraft.client.renderer.G;
import net.minecraft.logging.IProfiler;
import optifine.Config;
import optifine.CustomColors;
import vanilla.entity.boss.BossStatus;

public class ModuleBossStatus implements Module {

	@Override
	public void render(GuiIngame gui, float partialTicks, ScaledResolution res) {

		if (BossStatus.bossName == null || BossStatus.statusBarTime <= 0) return;

		IProfiler profiler = Minecraft.get().getProfiler();
		profiler.startSection("bossHealth");
		--BossStatus.statusBarTime;
		MCFontRenderer fontrenderer = MC.getFontRenderer();
		ScaledResolution scaledresolution = new ScaledResolution(MC.i());
		int i = scaledresolution.getScaledWidth();
		short short1 = 182;
		int j = i / 2 - short1 / 2;
		int k = (int) (BossStatus.healthScale * (float) (short1 + 1));
		byte b0 = 12;
		MC.getTextureManager().bindTexture(Gui.icons);
		gui.drawTexturedModalRect(j, b0, 0, 74, short1, 5);
		gui.drawTexturedModalRect(j, b0, 0, 74, short1, 5);
		if (k > 0) gui.drawTexturedModalRect(j, b0, 0, 79, k, 5);

		String s = BossStatus.bossName;
		int l = 0xffffff;

		if (Config.isCustomColors()) l = CustomColors.getBossTextColor(l);

//todo		gui.getFontRenderer().drawStringWithShadow(s, (float) (i / 2 - gui.getFontRenderer().getStringWidth(s) / 2), (float) (b0 - 10), l);
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		profiler.endSection();
	}

}
