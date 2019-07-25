package net.minecraft.client.gui.ingame;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.font.AssetsFontRenderer;
import net.minecraft.client.gui.map.Minimap;
import net.minecraft.client.renderer.BowPathRenderer;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.Lang;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.Settings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.Profiler;
import net.minecraft.util.*;
import optifine.Config;
import optifine.CustomColors;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class GuiIngame extends Gui {

	public static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
	private final Minecraft mc;
	public final RenderItem itemRenderer;

	/**
	 * ChatGUI instance that retains all previous chat data
	 */
	private final GuiNewChat persistantChatGUI;
	private int updateCounter;

	/**
	 * The string specifying which record music is playing
	 */
	private String recordPlaying = "";

	/**
	 * How many ticks the record playing message will be displayed
	 */
	private int recordPlayingUpFor;
	private boolean recordIsPlaying;


	/**
	 * Remaining ticks the item highlight should be visible
	 */
	private int remainingHighlightTicks;

	/**
	 * The ItemStack that is currently being highlighted
	 */
	private ItemStack highlightingItemStack;
	private final GuiOverlayDebug overlayDebug;

	/**
	 * The spectator GUI for this in-game GUI instance
	 */
	public final GuiSpectator spectatorGui;
	private final GuiPlayerTabOverlay overlayPlayerList;
	private int titleTicks;
	private String title = "";
	private String subtitle = "";
	private int fadeIn;
	private int titleDuration;
	private int fadeOut;

	private long loadingStarted = 0;
	private long loadingTime = 0;
	private String loading = null;


	public static String currentServer = "LOBBY_5";

	public GuiIngame(Minecraft mcIn) {
		this.mc = mcIn;
		this.itemRenderer = mcIn.getRenderItem();
		this.overlayDebug = new GuiOverlayDebug(mcIn);
		this.spectatorGui = new GuiSpectator(mcIn);
		this.persistantChatGUI = new GuiNewChat(mcIn);
		this.overlayPlayerList = new GuiPlayerTabOverlay(mcIn, this);
		this.resetTitle();
	}

	public void resetTitle() {
		this.fadeIn = 10;
		this.titleDuration = 70;
		this.fadeOut = 20;
	}

	public void renderGameOverlay(float partialTicks) {
		ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		int width = scaledresolution.getScaledWidth();
		int height = scaledresolution.getScaledHeight();
		this.mc.entityRenderer.setupOverlayRendering();
		G.enableBlend();

		for (Map.Entry<String, Module> e : Modules.getEntries()) {
			Profiler.in.startSection(e.getKey());
			e.getValue().render(this, partialTicks, scaledresolution);
			Profiler.in.endSection();
		}

		// Затемнение по краям экрана
		//		renderVignette(this.mc.thePlayer.getBrightness(partialTicks), scaledresolution);

		// Рисунок тыквы, надетой на голову
		//		renderPumpkinOverlay(scaledresolution);

		// Фиолетовый эффект портала
		//		renderPortal(scaledresolution, partialTicks);

		// Текст над инвентарём
		//renderTooltip1(scaledresolution, partialTicks);

		// Крестик в центре экрана
		renderCrosshair(width, height);

		// Боссбар
		//		renderBossHealth();

		// Броня, еда, здоровье
		//		renderPlayerStats(scaledresolution);

		G.disableBlend();

		// Затемнение экрана на кровати
		renderSleeping(width, height);

		// Полоска с опытом / силой прыжка на лошади
		renderBar(scaledresolution, width);

		// Интерфейс наблюдателя
		renderTooltip0(scaledresolution);

		// Загрузка (Таймер из текстерии)
		renderLoading(scaledresolution);

		// Экран отладки (F3)
		overlayDebug.renderDebugInfo(scaledresolution);

		// Название играющей пластинки
		renderRecord(partialTicks, width, height);

		// Иконка огня
		renderFireIcon(width, height);

		// Тайтл
		renderTitle(partialTicks, width, height);

		// Скорборд
		renderScoreboard(scaledresolution);

		// Чат
		renderChat(height);

		// Информация о эффектах
		if (Settings.FINE_EFFECTS.b()) InventoryEffectRenderer.drawActivePotionEffects(this, mc, getFontRenderer());

		// Информация о траектории полёта стрелы
		BowPathRenderer.renderOverlay(scaledresolution.getScaledWidth() / 4 - 80, scaledresolution.getScaledHeight() / 4 - 10);

		renderMinimap();

		//		renderFakeVime(scaledresolution, width, height);
	}


	public static IBlockState[][][] map;
	public static DynamicTexture texture;
	public static BlockPos[][][] mapblocks;
	private static long lastUpdatedTexture;

	private void generateTexture() {
		for (int x = MC.getPlayer().chunkCoordX - 2; x < MC.getPlayer().chunkCoordX + 3; x++)
			for (int z = MC.getPlayer().chunkCoordZ - 2; z < MC.getPlayer().chunkCoordZ + 3; z++)
				Minimap.initChunk(MC.getWorld().getChunkFromChunkCoords(x, z));
		lastUpdatedTexture = System.currentTimeMillis() + 100;
	}

	private void renderMinimap() {
		if (true) return;
		if (lastUpdatedTexture < System.currentTimeMillis()) generateTexture();

		Minimap.renderMinimap();
		//G.bindTexture(texture.getGlTextureId());
		//drawScaledCustomSizeModalRect(5, 5, 0, 0, 16, 16, 64, 64, 16, 16);


		//		IBlockState state = Blocks.clay.getDefaultState();
		//		IBakedModel model = MC.i().getModelManager().getBlockModelShapes().getModelForState(state);
		//		BakedQuad quad = model.getFaceQuads(EnumFacing.UP).get(0);
		//		MC.bindTexture(TextureMap.locationBlocksTexture);
		//		int[] vertexData = quad.getVertexData();
		//
		//		r.begin(7, DefaultVertexFormats.POSITION_TEX);
		//		for (int i = 0; i < 4; i++) {
		//			int j = i * 7;
		//			r.pos(toFloat(vertexData[j]) * 16, toFloat(vertexData[j + 2]) * 16, 0).tex(toFloat(vertexData[j + 4]), toFloat(vertexData[j + 5])).endVertex();
		//			System.out.print("\n");
		//		}
		//
		//		Tessellator.getInstance().draw();
		/*
		int factor = 1;
		float antifactor = 2F / factor;

		GlStateManager.pushMatrix();

		MC.bindTexture(TextureMap.locationBlocksTexture);
		G.enableBlend();
		GlStateManager.scale(factor, factor, 0);
		for (int x = 0; x < map.length; x++) {
			IBlockState[][] is = map[x];
			int height = 0;
			if (is != null) for (int z = 0; z < (height = is.length); z++) {
				IBlockState[] states = is[z];
				if (states != null) {

					for (int y = 0; y < states.length; y++) {
						IBlockState state = states[y];

						BlockPos pos = mapblocks[x][z][y];
						BlockRendererDispatcher d = MC.i().getBlockRendererDispatcher();
						IBakedModel model = d.getModelFromBlockState(state, MC.getWorld(), pos);


						//						IBakedModel model = MC.i().getModelManager().getBlockModelShapes().getModelForState(state);
						List<BakedQuad> faceQuads = model.getFaceQuads(EnumFacing.UP);
						List<BakedQuad> generalQuads = model.getGeneralQuads();
						List<BakedQuad> quads = new ArrayList<>();
						quads.addAll(generalQuads);
						quads.addAll(faceQuads);
						int color = -1;
						if (state.getBlock() instanceof BlockGrass) color = MC.getWorld().getWorldChunkManager().getBiome(pos).getGrassColorAtPos(pos);
						if (state.getBlock() instanceof BlockLeaves) {
							BlockPlanks.EnumType type = state.getValue(BlockOldLeaf.VARIANT);
							if (type == BlockPlanks.EnumType.BIRCH) color = ColorizerFoliage.getFoliageColorBirch();
							else if (type == BlockPlanks.EnumType.SPRUCE) color = ColorizerFoliage.getFoliageColorPine();
							else color = MC.getWorld().getWorldChunkManager().getBiome(pos).getFoliageColorAtPos(pos);

						}

						RenderEnv env = RenderEnv.getInstance(MC.getWorld(), state, pos);
						for (BakedQuad quad : quads) {

							int[] vertexData = quad.getVertexData();
							for (int s = 0; s < 2; s++) {
								GlStateManager.pushMatrix();
								if (s == 0) {
									GlStateManager.color(0, 0, 0, 0.3f);
									GlStateManager.translate(antifactor, antifactor, 0);
								}
								else Utils.glColorNoAlpha(color);
								if (state.getBlock() instanceof BlockStainedGlass) {

									quad = ConnectedTextures.getConnectedTexture(MC.getWorld(), state, pos, quad, env);
								} else {
//									MC.bindTexture(TextureMap.locationBlocksTexture);
								}
								r.putSprite(quad.getSprite());
								r.begin(7, DefaultVertexFormats.POSITION_TEX);
//								if (quad.getSprite().glSpriteTextureId != -1) quad.getSprite().bindSpriteTexture();
								for (int i = 0; i < 4; i++) {
									int j = i * 7;
									r.pos(toFloat(vertexData[j]), toFloat(vertexData[j + 2]), 0)
											.tex(toFloat(vertexData[j + 4]), toFloat(vertexData[j + 5])).endVertex();
								}
								Tessellator.getInstance().draw();
								GlStateManager.popMatrix();

							}

					}

					}
				}
				G.translate(0, 1, 0);

			}
			G.translate(1, -height, 0);
		}
		GlStateManager.popMatrix();
		*/
	}

	private void renderBar(ScaledResolution scaledresolution, int width) {
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		int i2 = width / 2 - 91;

		if (this.mc.thePlayer.isRidingHorse()) {
			this.renderHorseJumpBar(scaledresolution, i2);
		} else if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
			this.renderExpBar(scaledresolution, i2);
		}
	}

	private void renderCrosshair(int width, int height) {
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(icons);
		G.enableBlend();

		if (this.showCrosshair() && Settings.getPerspective() < 1) {
			G.tryBlendFuncSeparate(775, 769, 1, 0);
			G.enableAlpha();
			this.drawTexturedModalRect(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
		}

		G.tryBlendFuncSeparate(770, 771, 1, 0);
	}


	private void renderTooltip0(ScaledResolution scaledresolution) {
		if (Settings.HELD_ITEM_TOOLTIPS.b() && !this.mc.playerController.isSpectator()) this.renderTooltip(scaledresolution);
		else if (this.mc.thePlayer.isSpectator()) this.spectatorGui.render(scaledresolution);
	}

	private void renderChat(int height) {
		G.enableBlend();
		G.tryBlendFuncSeparate(770, 771, 1, 0);
		G.disableAlpha();
		G.pushMatrix();
		G.translate(0.0F, (float) (height - 48), 0.0F);
		Profiler.in.startSection("chat");
		this.persistantChatGUI.drawChat(this.updateCounter);
		Profiler.in.endSection();
		G.popMatrix();

		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		G.disableLighting();
		G.enableAlpha();
	}

	private void renderScoreboard(ScaledResolution scaledresolution) {
		Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
		ScoreObjective obj = null;
		ScorePlayerTeam team = scoreboard.getPlayersTeam(this.mc.thePlayer.getName());

		if (team != null) {
			int j1 = team.getChatFormat().getColorIndex();
			if (j1 >= 0) obj = scoreboard.getObjectiveInDisplaySlot(3 + j1);
		}

		ScoreObjective slotObj = obj != null ? obj : scoreboard.getObjectiveInDisplaySlot(1);
		if (slotObj != null) this.renderScoreboard(slotObj, scaledresolution);

		slotObj = scoreboard.getObjectiveInDisplaySlot(0);

		if (!KeyBinding.PLAYERLIST.isKeyDown() || this.mc.isIntegratedServerRunning() && this.mc.thePlayer.sendQueue.getPlayerInfoMap().size() <= 1 && slotObj == null) {
			this.overlayPlayerList.updatePlayerList(false);
		} else {
			this.overlayPlayerList.updatePlayerList(true);
			this.overlayPlayerList.renderPlayerlist(scaledresolution.getScaledWidth(), scoreboard, slotObj);
		}
	}

	private void renderSleeping(int width, int height) {
		if (this.mc.thePlayer.getSleepTimer() <= 0) return;
		Profiler.in.startSection("sleep");
		G.disableDepth();
		G.disableAlpha();
		int l = this.mc.thePlayer.getSleepTimer();
		float f2 = (float) l / 100.0F;

		if (f2 > 1.0F) {
			f2 = 1.0F - (float) (l - 100) / 10.0F;
		}

		int k = (int) (220.0F * f2) << 24 | 1052704;
		drawRect(0, 0, width, height, k);
		G.enableAlpha();
		G.enableDepth();
		Profiler.in.endSection();
	}

	private void renderFireIcon(int width, int height) {
		if (!mc.thePlayer.isBurning() || Settings.RENDER_FIRE.i() != 1) return;

		TextureAtlasSprite textureatlassprite = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
		this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		G.color(1, 1, 1, 1);
		drawTexturedModalRect(width / 2 - 16, height / 2, textureatlassprite, 16, 16);
	}

	private void renderLoading(ScaledResolution scaledresolution) {
		if (this.loading == null) return;
		long time = System.currentTimeMillis();
		float percentage = (float) (time - loadingStarted) / loadingTime;
		if (percentage > 1.1) {
			loading = null;
			loadingTime = 0;
			loadingStarted = 0;
		} else {
			int x1 = scaledresolution.getScaledWidth() / 2 - 100;
			int x2 = x1 + 200;
			int x = (int) (x1 + percentage * 200);
			int y = scaledresolution.getScaledHeight() - 64;

			int opacity = 0xc0;
			if (percentage < 0.1) opacity *= percentage * 10;
			if (percentage > 1.0) opacity *= (0.1 - (percentage - 1)) * 10;

			opacity <<= 24;
			if (percentage > 1) x = x2;

			drawRect(x1, y, x2, y + 10, 0xffffff | opacity);
			drawRect(x1, y, x, y + 10, 0xf93eed | opacity);
			drawCenteredString(mc.fontRenderer, loading, x1 + 100, y - 15, 0xffffff | opacity);
		}
	}

	private void renderRecord(float partialTicks, int width, int height) {
		if (this.recordPlayingUpFor <= 0) return;
		Profiler.in.startSection("overlayMessage");
		float f3 = (float) this.recordPlayingUpFor - partialTicks;
		int k1 = (int) (f3 * 255.0F / 20.0F);

		if (k1 > 255) {
			k1 = 255;
		}

		if (k1 > 8) {
			G.pushMatrix();
			G.translate((float) (width / 2), (float) (height - 68), 0.0F);
			G.enableBlend();
			G.tryBlendFuncSeparate(770, 771, 1, 0);
			int i1 = 16777215;

			if (this.recordIsPlaying) {
				i1 = MathHelper.func_181758_c(f3 / 50.0F, 0.7F, 0.6F) & 16777215;
			}

			this.getFontRenderer().drawString(this.recordPlaying, -this.getFontRenderer().getStringWidth(this.recordPlaying) / 2, -4, i1 + (k1 << 24 & -16777216));
			G.disableBlend();
			G.popMatrix();
		}

		Profiler.in.endSection();
	}

	public void renderTitle(float partialTicks, int width, int height) {
		if (titleTicks <= 0) return;
		Profiler.in.startSection("titleAndSubtitle");
		float f4 = (float) this.titleTicks - partialTicks;
		int opacity = 0xff;

		if (this.titleTicks > this.fadeOut + this.titleDuration) {
			float f1 = (float) (this.fadeIn + this.titleDuration + this.fadeOut) - f4;
			opacity = (int) (f1 * 255f / (float) this.fadeIn);
		}

		if (this.titleTicks <= this.fadeOut) opacity = (int) (f4 * 255f / (float) this.fadeOut);

		opacity = MathHelper.clamp_int(opacity, 0, 255);

		if (opacity > 8) {
			G.pushMatrix();
			G.translate((float) (width / 2), (float) (height / 2), 0.0F);
			G.enableBlend();
			G.tryBlendFuncSeparate(770, 771, 1, 0);
			G.pushMatrix();
			G.scale(4.0F, 4.0F, 4.0F);
			int j2 = opacity << 24 & 0xff000000;
			this.getFontRenderer().drawString(this.title, (float) (-this.getFontRenderer().getStringWidth(this.title) / 2), -10.0F, 0xffffff | j2, true);
			G.popMatrix();
			G.pushMatrix();
			G.scale(2.0F, 2.0F, 2.0F);
			this.getFontRenderer().drawString(this.subtitle, (float) (-this.getFontRenderer().getStringWidth(this.subtitle) / 2), 5.0F, 0xffffff | j2, true);
			G.popMatrix();
			G.disableBlend();
			G.popMatrix();
		}

		Profiler.in.endSection();
	}

	public void renderHorseJumpBar(ScaledResolution p_175186_1_, int p_175186_2_) {
		Profiler.in.startSection("jumpBar");
		this.mc.getTextureManager().bindTexture(Gui.icons);
		float f = this.mc.thePlayer.getHorseJumpPower();
		short short1 = 182;
		int i = (int) (f * (float) (short1 + 1));
		int j = p_175186_1_.getScaledHeight() - 32 + 3;
		this.drawTexturedModalRect(p_175186_2_, j, 0, 84, short1, 5);

		if (i > 0) {
			this.drawTexturedModalRect(p_175186_2_, j, 0, 89, i, 5);
		}

		Profiler.in.endSection();
	}

	public void renderExpBar(ScaledResolution p_175176_1_, int p_175176_2_) {
		Profiler.in.startSection("expBar");
		this.mc.getTextureManager().bindTexture(Gui.icons);
		int i = this.mc.thePlayer.xpBarCap();

		if (i > 0) {
			short short1 = 182;
			int k = (int) (this.mc.thePlayer.experience * (float) (short1 + 1));
			int j = p_175176_1_.getScaledHeight() - 32 + 3;
			this.drawTexturedModalRect(p_175176_2_, j, 0, 64, short1, 5);

			if (k > 0) {
				this.drawTexturedModalRect(p_175176_2_, j, 0, 69, k, 5);
			}
		}

		Profiler.in.endSection();

		if (this.mc.thePlayer.experienceLevel > 0) {
			Profiler.in.startSection("expLevel");
			int j1 = 8453920;

			if (Config.isCustomColors()) {
				j1 = CustomColors.getExpBarTextColor(j1);
			}

			String s = "" + this.mc.thePlayer.experienceLevel;
			int i1 = (p_175176_1_.getScaledWidth() - this.getFontRenderer().getStringWidth(s)) / 2;
			int l = p_175176_1_.getScaledHeight() - 31 - 4;
			boolean flag = false;
			this.getFontRenderer().drawString(s, i1 + 1, l, 0);
			this.getFontRenderer().drawString(s, i1 - 1, l, 0);
			this.getFontRenderer().drawString(s, i1, l + 1, 0);
			this.getFontRenderer().drawString(s, i1, l - 1, 0);
			this.getFontRenderer().drawString(s, i1, l, j1);
			Profiler.in.endSection();
		}
	}

	public void renderTooltip(ScaledResolution res) {
		if (this.remainingHighlightTicks <= 0 || this.highlightingItemStack == null) return;
		Profiler.in.startSection("selectedItemName");

		String s = this.highlightingItemStack.getDisplayName();

		int i = (res.getScaledWidth() - this.getFontRenderer().getStringWidth(s)) / 2;
		int j = res.getScaledHeight() - 59;

		if (!this.mc.playerController.shouldDrawHUD()) j += 14;

		int k = (int) ((float) this.remainingHighlightTicks * 256.0F / 10.0F);
		if (k > 255) k = 255;

		if (k > 0) {
			G.pushMatrix();
			G.enableBlend();
			G.tryBlendFuncSeparate(770, 771, 1, 0);
			this.getFontRenderer().drawStringWithShadow(s, (float) i, (float) j, 16777215 + (k << 24));
			G.disableBlend();
			G.popMatrix();
		}
		Profiler.in.endSection();

	}

	protected boolean showCrosshair() {
		if (Settings.SHOW_DEBUG.b() && !this.mc.thePlayer.hasReducedDebug() && !(Settings.REDUCED_DEBUG_INFO.i() > 0)) {
			return false;
		}
		if (this.mc.playerController.isSpectator()) {
			if (this.mc.pointedEntity != null) {
				return true;
			}
			if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();

				return this.mc.theWorld.getTileEntity(blockpos) instanceof IInventory;
			}

			return false;
		}
		return true;
	}


	private void renderScoreboard(ScoreObjective sidebarObjective, ScaledResolution res) {
		Scoreboard scoreboard = sidebarObjective.getScoreboard();
		Collection<Score> scores = scoreboard.getSortedScores(sidebarObjective);
		List<Score> arraylist = scores.stream().filter(s -> s.getPlayerName() != null && !s.getPlayerName().startsWith("#")).collect(Collectors.toList());
		List<Score> scoreboardLines;

		if (arraylist.size() > 15) scoreboardLines = Lists.newArrayList(Iterables.skip(arraylist, scores.size() - 15));
		else scoreboardLines = arraylist;

		int i = this.getFontRenderer().getStringWidth(sidebarObjective.getDisplayName());

		for (Object score : scoreboardLines) {
			ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(((Score) score).getPlayerName());
			String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, ((Score) score).getPlayerName()) + ": " + EnumChatFormatting.RED + ((Score) score).getScorePoints();
			i = Math.max(i, this.getFontRenderer().getStringWidth(s));
		}

		int j1 = scoreboardLines.size() * this.getFontRenderer().getFontHeight();
		int k1 = res.getScaledHeight() / 2 + j1 / 3;
		byte b0 = 3;
		int j = res.getScaledWidth() - i - b0;
		int k = 0;

		for (Object score1 : scoreboardLines) {
			++k;
			ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(((Score) score1).getPlayerName());
			String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, ((Score) score1).getPlayerName());
			String s2 = EnumChatFormatting.RED + "" + ((Score) score1).getScorePoints();
			int l = k1 - k * this.getFontRenderer().getFontHeight();
			int i1 = res.getScaledWidth() - b0 + 2;
			drawRect(j - 2, l, i1, l + this.getFontRenderer().getFontHeight(), 1342177280);
			this.getFontRenderer().drawString(s1, j, l, 553648127);
			this.getFontRenderer().drawString(s2, i1 - this.getFontRenderer().getStringWidth(s2), l, 553648127);

			if (k == scoreboardLines.size()) {
				String s3 = sidebarObjective.getDisplayName();
				drawRect(j - 2, l - this.getFontRenderer().getFontHeight() - 1, i1, l - 1, 1610612736);
				drawRect(j - 2, l - 1, i1, l, 1342177280);
				this.getFontRenderer().drawString(s3, j + i / 2 - this.getFontRenderer().getStringWidth(s3) / 2, l - this.getFontRenderer().getFontHeight(), 553648127);
			}
		}
	}


	/**
	 * The update tick for the ingame UI
	 */
	public void updateTick() {
		if (this.recordPlayingUpFor > 0) --this.recordPlayingUpFor;

		if (this.titleTicks > 0) {
			--this.titleTicks;

			if (this.titleTicks <= 0) {
				this.title = "";
				this.subtitle = "";
			}
		}

		++this.updateCounter;

		if (this.mc.thePlayer != null) {
			ItemStack itemstack = this.mc.thePlayer.inventory.getCurrentItem();

			if (itemstack == null) {
				this.remainingHighlightTicks = 0;
			} else if (this.highlightingItemStack != null && itemstack.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(itemstack,
					this.highlightingItemStack) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == this.highlightingItemStack.getMetadata())) {
				if (this.remainingHighlightTicks > 0) {
					--this.remainingHighlightTicks;
				}
			} else {
				this.remainingHighlightTicks = 40;
			}

			this.highlightingItemStack = itemstack;
		}
	}

	public void setRecordPlayingMessage(String record) {
		this.setRecordPlaying(Lang.format("record.nowPlaying", record), true);
	}

	public void setRecordPlaying(String p_110326_1_, boolean p_110326_2_) {
		this.recordPlaying = p_110326_1_;
		this.recordPlayingUpFor = 60;
		this.recordIsPlaying = p_110326_2_;
	}

	public void displayTitle(String top, String sub, int fadeIn, int duration, int fadeOut) {
		if (top == null && sub == null && fadeIn < 0 && duration < 0 && fadeOut < 0) {
			this.title = "";
			this.subtitle = "";
			this.titleTicks = 0;
		} else if (top != null) {
			this.title = top;
			this.titleTicks = this.fadeIn + this.titleDuration + this.fadeOut;
		} else if (sub != null) {
			this.subtitle = sub;
		} else {
			if (fadeIn >= 0) this.fadeIn = fadeIn;

			if (duration >= 0) this.titleDuration = duration;

			if (fadeOut >= 0) this.fadeOut = fadeOut;

			if (this.titleTicks > 0)
				this.titleTicks = this.fadeIn + this.titleDuration + this.fadeOut;
		}
	}

	public void setRecordPlaying(IChatComponent p_175188_1_, boolean p_175188_2_) {
		this.setRecordPlaying(p_175188_1_.getUnformattedText(), p_175188_2_);
	}

	/**
	 * returns a pointer to the persistant Chat GUI, containing all previous chat messages and such
	 */
	public GuiNewChat getChatGUI() {
		return this.persistantChatGUI;
	}

	public int getUpdateCounter() {
		return this.updateCounter;
	}

	public AssetsFontRenderer getFontRenderer() {
		return this.mc.fontRenderer;
	}

	public GuiSpectator getSpectatorGui() {
		return this.spectatorGui;
	}

	public GuiPlayerTabOverlay getTabList() {
		return this.overlayPlayerList;
	}

	public void func_181029_i() {
		this.overlayPlayerList.func_181030_a();
	}

	public void setLoading(long ms, String string) {
		this.loading = string;
		this.loadingTime = ms;
		this.loadingStarted = System.currentTimeMillis();
	}

}
