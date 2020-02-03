package net.minecraft.client.gui.spectator.categories;

import net.minecraft.client.Minecraft;
import net.minecraft.client.game.entity.AbstractClientPlayer;
import net.minecraft.client.gui.font.MCFontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.font.FontUtils;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.protocol.minecraft_47.NetworkPlayerInfo;
import net.minecraft.client.renderer.G;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.chat.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TeleportToTeam implements ISpectatorMenuView, ISpectatorMenuObject {

	private final List<ISpectatorMenuObject> objects = new ArrayList<>();

	public TeleportToTeam() {
		Minecraft minecraft = Minecraft.get();

		for (ScorePlayerTeam scoreplayerteam : minecraft.theWorld.getScoreboard().getTeams()) {
			this.objects.add(new TeleportToTeam.TeamSelectionObject(scoreplayerteam));
		}
	}

	public List<ISpectatorMenuObject> func_178669_a() {
		return this.objects;
	}

	public IChatComponent func_178670_b() {
		return new ChatComponentText("Select a team to teleport to");
	}

	public void func_178661_a(SpectatorMenu menu) {
		menu.func_178647_a(this);
	}

	public IChatComponent getSpectatorName() {
		return new ChatComponentText("Teleport to team member");
	}

	public void render(float p_178663_1_, int alpha) {
		Minecraft.get().getTextureManager().bindTexture(GuiSpectator.swidgetsResource);
		Gui.drawModalRectWithCustomSizedTexture(0, 0, 16.0F, 0.0F, 16, 16, 256.0F, 256.0F);
	}

	public boolean func_178662_A_() {
		for (ISpectatorMenuObject ispectatormenuobject : this.objects) {
			if (ispectatormenuobject.func_178662_A_()) {
				return true;
			}
		}

		return false;
	}

	class TeamSelectionObject implements ISpectatorMenuObject {

		private final ScorePlayerTeam team;
		private final ResourceLocation resource;
		private final List<NetworkPlayerInfo> playerInfos;

		public TeamSelectionObject(ScorePlayerTeam team) {
			this.team = team;
			this.playerInfos = new ArrayList<>();

			for (String s : team.getMembershipCollection()) {
				NetworkPlayerInfo networkplayerinfo = Minecraft.get().getNetHandler().getPlayerInfo(s);
				if (networkplayerinfo != null) this.playerInfos.add(networkplayerinfo);
			}

			if (this.playerInfos.isEmpty()) this.resource = DefaultPlayerSkin.getDefaultSkinLegacy();
			else {
				String s1 = this.playerInfos.get(new Random().nextInt(this.playerInfos.size())).getGameProfile().getName();
				this.resource = AbstractClientPlayer.getLocationSkin(s1);
				AbstractClientPlayer.getDownloadImageSkin(this.resource, s1);
			}
		}

		public void func_178661_a(SpectatorMenu menu) {
			menu.func_178647_a(new TeleportToPlayer(this.playerInfos));
		}

		public IChatComponent getSpectatorName() {
			return new ChatComponentText(this.team.getTeamName());
		}

		public void render(float p_178663_1_, int alpha) {
			int i = -1;
			String s = MCFontRenderer.getFormatFromString(this.team.getColorPrefix());

			if (s.length() >= 2) {
				i = FontUtils.getColorCode(s.charAt(1));
			}

			if (i >= 0) {
				float f = (float) (i >> 16 & 255) / 255.0F;
				float f1 = (float) (i >> 8 & 255) / 255.0F;
				float f2 = (float) (i & 255) / 255.0F;
				Gui.drawRect(1, 1, 15, 15, MathHelper.func_180183_b(f * p_178663_1_, f1 * p_178663_1_, f2 * p_178663_1_) | alpha << 24);
			}

			Minecraft.get().getTextureManager().bindTexture(this.resource);
			G.color(p_178663_1_, p_178663_1_, p_178663_1_, (float) alpha / 255.0F);
			Gui.drawScaledCustomSizeModalRect(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
			Gui.drawScaledCustomSizeModalRect(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
		}

		public boolean func_178662_A_() {
			return !this.playerInfos.isEmpty();
		}

	}

}
