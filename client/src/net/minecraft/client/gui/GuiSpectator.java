package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuRecipient;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class GuiSpectator extends Gui implements ISpectatorMenuRecipient {

	public static final ResourceLocation swidgetsResource = new ResourceLocation("textures/gui/spectator_widgets.png");
	private static final ResourceLocation widgetsResource = new ResourceLocation("textures/gui/widgets.png");
	private final Minecraft minecraft;
	private long time;
	private SpectatorMenu spectatorMenu;

	public GuiSpectator(Minecraft mcIn) {
		this.minecraft = mcIn;
	}

	public void func_175260_a(int p_175260_1_) {
		this.time = Minecraft.getSystemTime();

		if (this.spectatorMenu != null) {
			this.spectatorMenu.func_178644_b(p_175260_1_);
		} else {
			this.spectatorMenu = new SpectatorMenu(this);
		}
	}

	private float func_175265_c() {
		long i = this.time - Minecraft.getSystemTime() + 5000L;
		return MathHelper.clamp_float((float) i / 2000.0F, 0.0F, 1.0F);
	}

	public void renderTooltip(ScaledResolution res, float partialTicks) {
		if (this.spectatorMenu == null) return;
		float f = this.func_175265_c();

		if (f <= 0.0F) this.spectatorMenu.func_178641_d();
		else {
			int i = res.getScaledWidth() / 2;
			float f1 = this.zLevel;
			this.zLevel = -90.0F;
			float f2 = (float) res.getScaledHeight() - 22.0F * f;
			SpectatorDetails spectatordetails = this.spectatorMenu.func_178646_f();
			this.func_175258_a(res, f, i, f2, spectatordetails);
			this.zLevel = f1;
		}
	}

	protected void func_175258_a(ScaledResolution res, float p_175258_2_, int p_175258_3_, float p_175258_4_, SpectatorDetails details) {
		G.enableRescaleNormal();
		G.enableBlend();
		G.tryBlendFuncSeparate(770, 771, 1, 0);
		G.color(1.0F, 1.0F, 1.0F, p_175258_2_);
		this.minecraft.getTextureManager().bindTexture(widgetsResource);
		this.drawTexturedModalRect((float) (p_175258_3_ - 91), p_175258_4_, 0, 0, 182, 22);

		if (details.func_178681_b() >= 0) {
			this.drawTexturedModalRect((float) (p_175258_3_ - 91 - 1 + details.func_178681_b() * 20), p_175258_4_ - 1.0F, 0, 22, 24, 22);
		}

		RenderHelper.enableGUIStandardItemLighting();

		for (int i = 0; i < 9; ++i) {
			this.func_175266_a(i, res.getScaledWidth() / 2 - 90 + i * 20 + 2, p_175258_4_ + 3.0F, p_175258_2_, details.func_178680_a(i));
		}

		RenderHelper.disableStandardItemLighting();
		G.disableRescaleNormal();
		G.disableBlend();
	}

	private void func_175266_a(int key, int p_175266_2_, float p_175266_3_, float p_175266_4_, ISpectatorMenuObject menuObj) {
		this.minecraft.getTextureManager().bindTexture(swidgetsResource);

		if (menuObj != SpectatorMenu.field_178657_a) {
			int i = (int) (p_175266_4_ * 255.0F);
			G.pushMatrix();
			G.translate((float) p_175266_2_, p_175266_3_, 0.0F);
			float f = menuObj.func_178662_A_() ? 1.0F : 0.25F;
			G.color(f, f, f, p_175266_4_);
			menuObj.render(f, i);
			G.popMatrix();
			String s = KeyBinding.HOTBAR[key].getKeyDisplayString();

			if (i > 3 && menuObj.func_178662_A_()) {
				this.minecraft.fontRenderer.drawStringWithShadow(s, (float) (p_175266_2_ + 19 - 2 - this.minecraft.fontRenderer.getStringWidth(s)), p_175266_3_ + 6.0F + 3.0F, 16777215 + (i << 24));
			}
		}
	}

	public void render(ScaledResolution res) {
		//
		//		NetHandlerPlayClient nhpc = MC.getPlayer().sendQueue;
		//		Collection<NetworkPlayerInfo> list = nhpc.getPlayerInfoMap();
		//
		//		Map<ScorePlayerTeam, List<NetworkPlayerInfo>> teams = new HashMap<>();
		//		Collection<ScorePlayerTeam> ts = MC.getWorld().getScoreboard().getTeams();
		//
		//		for (ScorePlayerTeam t : ts) teams.put(t, new ArrayList<>());
		//		for (NetworkPlayerInfo playerInfo : list) {
		//			if (playerInfo.getPlayerTeam() != null && teams.containsKey(playerInfo.getPlayerTeam()))
		//				teams.get(playerInfo.getPlayerTeam()).add(playerInfo);
		//		}
		//
		//
		//		for (ScorePlayerTeam team : teams.keySet()) {
		//			String s = FontRenderer.getFormatFromString(team.getColorPrefix());
		//			int i = -1;
		//			if (s.length() >= 2) i = Minecraft.getMinecraft().fontRenderer.getColorCode(s.charAt(1));
		//			drawRect(0, 2, 100, 8, i);
		//
		//			for (NetworkPlayerInfo player : teams.get(team)) {
		//
		//
		//				GameProfile gameprofile = player.getGameProfile();
		//
		//				EntityPlayer entityplayer = MC.getWorld().getPlayerEntityByUUID(gameprofile.getId());
		//				MC.i().getTextureManager().bindTexture(player.getLocationSkin());
		//				Gui.drawScaledCustomSizeModalRect(100, 100, 8.0F, (float) 8, 8, 8, 8, 8, 64.0F, 64.0F);
		//
		//				if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
		//					Gui.drawScaledCustomSizeModalRect(100, 100, 40.0F, (float) 8, 8, 8, 8, 8, 64.0F, 64.0F);
		//				}
		//
		//			}
		//
		//		}


		int i = (int) (this.func_175265_c() * 255.0F);

		if (i > 3 && this.spectatorMenu != null) {
			ISpectatorMenuObject ispectatormenuobject = this.spectatorMenu.func_178645_b();
			String s = ispectatormenuobject != SpectatorMenu.field_178657_a ? ispectatormenuobject.getSpectatorName().getFormattedText() : this.spectatorMenu.func_178650_c().func_178670_b().getFormattedText();

			if (s != null) {
				int j = (res.getScaledWidth() - this.minecraft.fontRenderer.getStringWidth(s)) / 2;
				int k = res.getScaledHeight() - 35;
				G.pushMatrix();
				G.enableBlend();
				G.tryBlendFuncSeparate(770, 771, 1, 0);
				this.minecraft.fontRenderer.drawStringWithShadow(s, (float) j, (float) k, 16777215 + (i << 24));
				G.disableBlend();
				G.popMatrix();
			}
		}
	}

	public void func_175257_a(SpectatorMenu p_175257_1_) {
		this.spectatorMenu = null;
		this.time = 0L;
	}

	public boolean func_175262_a() {
		return this.spectatorMenu != null;
	}

	public void func_175259_b(int p_175259_1_) {
		int i;

		for (i = this.spectatorMenu.func_178648_e() + p_175259_1_; i >= 0 && i <= 8 && (this.spectatorMenu.func_178643_a(i) == SpectatorMenu.field_178657_a || !this.spectatorMenu.func_178643_a(
				i).func_178662_A_()); i += p_175259_1_) {
		}

		if (i >= 0 && i <= 8) {
			this.spectatorMenu.func_178644_b(i);
			this.time = Minecraft.getSystemTime();
		}
	}

	public void func_175261_b() {
		this.time = Minecraft.getSystemTime();

		if (this.func_175262_a()) {
			int i = this.spectatorMenu.func_178648_e();

			if (i != -1) {
				this.spectatorMenu.func_178644_b(i);
			}
		} else {
			this.spectatorMenu = new SpectatorMenu(this);
		}
	}

}
