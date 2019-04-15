package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.Utils;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.AssetsFontRenderer;
import net.minecraft.client.keystrokes.KeyStroke;
import net.minecraft.client.keystrokes.KeyStrokes;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.Lang;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.Settings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.Profiler;
import net.minecraft.util.*;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.border.WorldBorder;
import optifine.Config;
import optifine.CustomColors;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class GuiIngame extends Gui {
	private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
	private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
	private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
	private final Random rand = new Random();
	private final Minecraft mc;
	private final RenderItem itemRenderer;

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
	 * Previous frame vignette brightness (slowly changes by 1% each frame)
	 */
	public float prevVignetteBrightness = 1.0F;

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
	private final GuiSpectator spectatorGui;
	private final GuiPlayerTabOverlay overlayPlayerList;
	private int titleTicks;
	private String title = "";
	private String subtitle = "";
	private int fadeIn;
	private int titleDuration;
	private int fadeOut;
	private int playerHealth = 0;
	private int lastPlayerHealth = 0;

	private long loadingStarted = 0;
	private long loadingTime = 0;
	private String loading = null;

	/**
	 * The last recorded system time
	 */
	private long lastSystemTime = 0L;

	/**
	 * Used with updateCounter to make the heart bar flash
	 */
	private long healthUpdateCounter = 0L;
	public static String currentServer = "LOBBY_5";
	public static long launchTime = System.currentTimeMillis();

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


		// Затемнение по краям экрана
		renderVignette(this.mc.thePlayer.getBrightness(partialTicks), scaledresolution);

		// Рисунок тыквы, надетой на голову
		renderPumpkinOverlay(scaledresolution);

		// Фиолетовый эффект портала
		renderPortal(scaledresolution, partialTicks);

		// Текст над инвентарём
		renderTooltip1(scaledresolution, partialTicks);

		// Крестик в центре экрана
		renderCrosshair(width, height);

		// Боссбар
		renderBossHealth();

		// Броня, еда, здоровье
		renderPlayerStats(scaledresolution);

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

		// Кейстроксы
		renderKeyStrokes();

		// Информация о траектории полёта стрелы
		BowPathRenderer.renderOverlay(scaledresolution.getScaledWidth() / 4 - 80, scaledresolution.getScaledHeight() / 4 - 10);

		renderMinimap();

//		renderFakeVime(scaledresolution, width, height);
	}


	public static IBlockState[][][] map;
	public static BlockPos[][][] mapblocks;

	private void renderMinimap() {

		if (map == null) return;


		WorldRenderer r = Tessellator.getInstance().getWorldRenderer();


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

		int factor = 16;
		float antifactor = 1F / factor;

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
						IBakedModel model = MC.i().getBlockRendererDispatcher().getModelFromBlockState(state, MC.getWorld(), pos);
						List<BakedQuad> faceQuads = model.getFaceQuads(EnumFacing.UP);
						List<BakedQuad> generalQuads = model.getGeneralQuads();
						List<BakedQuad> quads = new ArrayList<>();
						quads.addAll(generalQuads);
						quads.addAll(faceQuads);
						int color = -1;
						if (state.getBlock() instanceof BlockGrass) color = MC.getWorld().getWorldChunkManager().getBiomeGenerator(pos).getGrassColorAtPos(pos);
						if (state.getBlock() instanceof BlockLeaves) {
							BlockPlanks.EnumType type = state.getValue(BlockOldLeaf.VARIANT);
							if (type == BlockPlanks.EnumType.BIRCH) color = ColorizerFoliage.getFoliageColorBirch();
							else if (type == BlockPlanks.EnumType.SPRUCE) color = ColorizerFoliage.getFoliageColorPine();
							else color = MC.getWorld().getWorldChunkManager().getBiomeGenerator(pos).getFoliageColorAtPos(pos);
						}
						float shadowLong = 0;
						for (BakedQuad quad : quads) {

							int[] vertexData = quad.getVertexData();
							for (int i = 0; i < 4; i++) {
								float delta = toFloat(vertexData[i * 7 + 1]);
								if (delta > shadowLong) shadowLong = delta;
							}


						}

						shadowLong *= antifactor * 4;

						for (BakedQuad quad : quads) {

							int[] vertexData = quad.getVertexData();
							for (int s = 0; s < 2; s++) {
								GlStateManager.pushMatrix();
								if (s == 0) {
									GlStateManager.color(0, 0, 0, 0.3f);
									GlStateManager.translate(shadowLong, shadowLong, 0);
								}
								else Utils.glColorNoAlpha(color);
								r.begin(7, DefaultVertexFormats.POSITION_TEX);
								if (quad.getSprite().glSpriteTextureId != -1) quad.getSprite().bindSpriteTexture();
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

	}

	static ByteBuffer fourbytebuffer = ByteBuffer.allocate(4);
	public float toFloat(int i) {
		fourbytebuffer.putInt(i);
		fourbytebuffer.position(0);
		float f = fourbytebuffer.asFloatBuffer().get();
		fourbytebuffer.clear();
//		System.out.print(f + "  ");
		return f;
	}

	private void renderFakeVime(ScaledResolution scaledresolution, int width, int height) {

		MC.FR.drawString("§f[§e41§f] §f" + MC.getPlayer().getName(), 2, 2, 0xffffff, true);
		MC.FR.drawString(currentServer, 2, 4 + MC.FR.getFontHeight(), 0xffffff, true);
		MC.FR.drawString("§e[§dx4.4§e] §fКоличество коинов: §e45649", 2, height - 16 - MC.FR.getFontHeight(), 0xffffff, true);

		long time = System.currentTimeMillis();
		int duration = 3600 - (int) (time - launchTime) / 1000;
		if (duration > 3600 || duration < 0) {
			launchTime = time;
			duration = 0;
		}
		double progress = (double) duration / 3600;
		G.translate(0.5, 0, 0);
		drawProgressBar(width / 2 - 100, 13, 200, 3, progress);
		G.translate(-0.5, 0, 0);
		int minutes = duration / 60;
		int seconds = duration % 60;
		drawCenteredString(MC.FR, "До конца игры: §e" + minutes + ":" + (seconds < 10 ? "0" + seconds : seconds) , width / 2, 3, 0xffffff);

	}

	private void drawProgressBar(int x, int y, int width, int height, double progress) {
		int x1 = x + (int) ((double) width * progress);

		drawRect(x, y, x1, y + height, 0xc0009cff);
		drawRect(x1, y, x + width, y + height, 0xc0ffffff);

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

	private void renderTooltip1(ScaledResolution scaledresolution, float partialTicks) {
		if (this.mc.playerController.isSpectator()) this.spectatorGui.renderTooltip(scaledresolution, partialTicks);
		else this.renderTooltip(scaledresolution, partialTicks);
	}

	private void renderTooltip0(ScaledResolution scaledresolution) {
		if (Settings.HELD_ITEM_TOOLTIPS.b() && !this.mc.playerController.isSpectator()) this.renderTooltip(scaledresolution);
		else if (this.mc.thePlayer.isSpectator()) this.spectatorGui.render(scaledresolution);
	}

	private void renderKeyStrokes() {
		for (KeyStroke stroke : KeyStrokes.strokes) stroke.render(this);
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

	private void renderPortal(ScaledResolution scaledresolution, float partialTicks) {
		if (this.mc.thePlayer.isPotionActive(Potion.confusion)) return;
		float f = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;
		if (f > 0) this.func_180474_b(f, scaledresolution);
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
			drawCenteredString(mc.fontRendererObj, loading, x1 + 100, y - 15, 0xffffff | opacity);
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

	protected void renderTooltip(ScaledResolution sr, float partialTicks) {
		if (!(this.mc.getRenderViewEntity() instanceof EntityPlayer)) return;
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(widgetsTexPath);
		EntityPlayer entityplayer = (EntityPlayer) this.mc.getRenderViewEntity();
		int i = sr.getScaledWidth() / 2;
		float f = this.zLevel;
		this.zLevel = -90.0F;
		this.drawTexturedModalRect(i - 91, sr.getScaledHeight() - 22, 0, 0, 182, 22);
		this.drawTexturedModalRect(i - 91 - 1 + entityplayer.inventory.currentItem * 20, sr.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
		this.zLevel = f;
		G.enableRescaleNormal();
		G.enableBlend();
		G.tryBlendFuncSeparate(770, 771, 1, 0);
		RenderHelper.enableGUIStandardItemLighting();

		for (int j = 0; j < 9; ++j) {
			int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
			int l = sr.getScaledHeight() - 16 - 3;
			this.renderHotbarItem(j, k, l, partialTicks, entityplayer);
		}

		RenderHelper.disableStandardItemLighting();
		G.disableRescaleNormal();
		G.disableBlend();
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


	private void renderScoreboard(ScoreObjective p_180475_1_, ScaledResolution p_180475_2_) {
		Scoreboard scoreboard = p_180475_1_.getScoreboard();
		Collection collection = scoreboard.getSortedScores(p_180475_1_);
		ArrayList arraylist = Lists.newArrayList(Iterables.filter(collection, new Predicate() {


			public boolean apply(Score p_apply_1_) {
				return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
			}

			public boolean apply(Object p_apply_1_) {
				return this.apply((Score) p_apply_1_);
			}
		}));
		ArrayList arraylist1;

		if (arraylist.size() > 15) {
			arraylist1 = Lists.newArrayList(Iterables.skip(arraylist, collection.size() - 15));
		} else {
			arraylist1 = arraylist;
		}

		int i = this.getFontRenderer().getStringWidth(p_180475_1_.getDisplayName());

		for (Object score : arraylist1) {
			ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(((Score) score).getPlayerName());
			String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, ((Score) score).getPlayerName()) + ": " + EnumChatFormatting.RED + ((Score) score).getScorePoints();
			i = Math.max(i, this.getFontRenderer().getStringWidth(s));
		}

		int j1 = arraylist1.size() * this.getFontRenderer().getFontHeight();
		int k1 = p_180475_2_.getScaledHeight() / 2 + j1 / 3;
		byte b0 = 3;
		int j = p_180475_2_.getScaledWidth() - i - b0;
		int k = 0;

		for (Object score1 : arraylist1) {
			++k;
			ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(((Score) score1).getPlayerName());
			String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, ((Score) score1).getPlayerName());
			String s2 = EnumChatFormatting.RED + "" + ((Score) score1).getScorePoints();
			int l = k1 - k * this.getFontRenderer().getFontHeight();
			int i1 = p_180475_2_.getScaledWidth() - b0 + 2;
			drawRect(j - 2, l, i1, l + this.getFontRenderer().getFontHeight(), 1342177280);
			this.getFontRenderer().drawString(s1, j, l, 553648127);
			this.getFontRenderer().drawString(s2, i1 - this.getFontRenderer().getStringWidth(s2), l, 553648127);

			if (k == arraylist1.size()) {
				String s3 = p_180475_1_.getDisplayName();
				drawRect(j - 2, l - this.getFontRenderer().getFontHeight() - 1, i1, l - 1, 1610612736);
				drawRect(j - 2, l - 1, i1, l, 1342177280);
				this.getFontRenderer().drawString(s3, j + i / 2 - this.getFontRenderer().getStringWidth(s3) / 2, l - this.getFontRenderer().getFontHeight(), 553648127);
			}
		}
	}

	private void renderPlayerStats(ScaledResolution p_180477_1_) {
		if (!this.mc.playerController.shouldDrawHUD()) return;
		if (!(this.mc.getRenderViewEntity() instanceof EntityPlayer)) return;
		EntityPlayer entityplayer = (EntityPlayer) this.mc.getRenderViewEntity();
		int i = MathHelper.ceiling_float_int(entityplayer.getHealth());
		boolean flag = this.healthUpdateCounter > (long) this.updateCounter && (this.healthUpdateCounter - (long) this.updateCounter) / 3L % 2L == 1L;

		if (i < this.playerHealth && entityplayer.hurtResistantTime > 0) {
			this.lastSystemTime = Minecraft.getSystemTime();
			this.healthUpdateCounter = (long) (this.updateCounter + 20);
		} else if (i > this.playerHealth && entityplayer.hurtResistantTime > 0) {
			this.lastSystemTime = Minecraft.getSystemTime();
			this.healthUpdateCounter = (long) (this.updateCounter + 10);
		}

		if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
			this.playerHealth = i;
			this.lastPlayerHealth = i;
			this.lastSystemTime = Minecraft.getSystemTime();
		}

		this.playerHealth = i;
		int j = this.lastPlayerHealth;
		this.rand.setSeed((long) (this.updateCounter * 312871));
		boolean flag1 = false;
		FoodStats foodstats = entityplayer.getFoodStats();
		int k = foodstats.getFoodLevel();
		int l = foodstats.getPrevFoodLevel();
		IAttributeInstance iattributeinstance = entityplayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
		int i1 = p_180477_1_.getScaledWidth() / 2 - 91;
		int j1 = p_180477_1_.getScaledWidth() / 2 + 91;
		int k1 = p_180477_1_.getScaledHeight() - 39;
		float f = (float) iattributeinstance.getAttributeValue();
		float f1 = entityplayer.getAbsorptionAmount();
		int l1 = MathHelper.ceiling_float_int((f + f1) / 2.0F / 10.0F);
		int i2 = Math.max(10 - (l1 - 2), 3);
		int j2 = k1 - (l1 - 1) * i2 - 10;
		float f2 = f1;
		int k2 = entityplayer.getTotalArmorValue();
		int l2 = -1;

		if (entityplayer.isPotionActive(Potion.regeneration)) {
			l2 = this.updateCounter % MathHelper.ceiling_float_int(f + 5.0F);
		}

		Profiler.in.startSection("armor");

		for (int i3 = 0; i3 < 10; ++i3) {
			if (k2 > 0) {
				int j3 = i1 + i3 * 8;

				if (i3 * 2 + 1 < k2) {
					this.drawTexturedModalRect(j3, j2, 34, 9, 9, 9);
				}

				if (i3 * 2 + 1 == k2) {
					this.drawTexturedModalRect(j3, j2, 25, 9, 9, 9);
				}

				if (i3 * 2 + 1 > k2) {
					this.drawTexturedModalRect(j3, j2, 16, 9, 9, 9);
				}
			}
		}

		Profiler.in.endStartSection("health");

		for (int j5 = MathHelper.ceiling_float_int((f + f1) / 2.0F) - 1; j5 >= 0; --j5) {
			int k5 = 16;

			if (entityplayer.isPotionActive(Potion.poison)) {
				k5 += 36;
			} else if (entityplayer.isPotionActive(Potion.wither)) {
				k5 += 72;
			}

			byte b0 = 0;

			if (flag) {
				b0 = 1;
			}

			int k3 = MathHelper.ceiling_float_int((float) (j5 + 1) / 10.0F) - 1;
			int l3 = i1 + j5 % 10 * 8;
			int i4 = k1 - k3 * i2;

			if (i <= 4) {
				i4 += this.rand.nextInt(2);
			}

			if (j5 == l2) {
				i4 -= 2;
			}

			byte b1 = 0;

			if (entityplayer.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
				b1 = 5;
			}

			this.drawTexturedModalRect(l3, i4, 16 + b0 * 9, 9 * b1, 9, 9);

			if (flag) {
				if (j5 * 2 + 1 < j) {
					this.drawTexturedModalRect(l3, i4, k5 + 54, 9 * b1, 9, 9);
				}

				if (j5 * 2 + 1 == j) {
					this.drawTexturedModalRect(l3, i4, k5 + 63, 9 * b1, 9, 9);
				}
			}

			if (f2 <= 0.0F) {
				if (j5 * 2 + 1 < i) {
					this.drawTexturedModalRect(l3, i4, k5 + 36, 9 * b1, 9, 9);
				}

				if (j5 * 2 + 1 == i) {
					this.drawTexturedModalRect(l3, i4, k5 + 45, 9 * b1, 9, 9);
				}
			} else {
				if (f2 == f1 && f1 % 2.0F == 1.0F) {
					this.drawTexturedModalRect(l3, i4, k5 + 153, 9 * b1, 9, 9);
				} else {
					this.drawTexturedModalRect(l3, i4, k5 + 144, 9 * b1, 9, 9);
				}

				f2 -= 2.0F;
			}
		}

		Entity entity = entityplayer.ridingEntity;

		if (entity == null) {
			Profiler.in.endStartSection("food");

			for (int l5 = 0; l5 < 10; ++l5) {
				int i8 = k1;
				int j6 = 16;
				byte b4 = 0;

				if (entityplayer.isPotionActive(Potion.hunger)) {
					j6 += 36;
					b4 = 13;
				}

				if (entityplayer.getFoodStats().getSaturationLevel() <= 0.0F && this.updateCounter % (k * 3 + 1) == 0) {
					i8 = k1 + this.rand.nextInt(3) - 1;
				}

				if (flag1) {
					b4 = 1;
				}

				int k7 = j1 - l5 * 8 - 9;
				this.drawTexturedModalRect(k7, i8, 16 + b4 * 9, 27, 9, 9);

				if (flag1) {
					if (l5 * 2 + 1 < l) {
						this.drawTexturedModalRect(k7, i8, j6 + 54, 27, 9, 9);
					}

					if (l5 * 2 + 1 == l) {
						this.drawTexturedModalRect(k7, i8, j6 + 63, 27, 9, 9);
					}
				}

				if (l5 * 2 + 1 < k) {
					this.drawTexturedModalRect(k7, i8, j6 + 36, 27, 9, 9);
				}

				if (l5 * 2 + 1 == k) {
					this.drawTexturedModalRect(k7, i8, j6 + 45, 27, 9, 9);
				}
			}
		} else if (entity instanceof EntityLivingBase) {
			Profiler.in.endStartSection("mountHealth");
			EntityLivingBase entitylivingbase = (EntityLivingBase) entity;
			int l7 = (int) Math.ceil((double) entitylivingbase.getHealth());
			float f3 = entitylivingbase.getMaxHealth();
			int l6 = (int) (f3 + 0.5F) / 2;

			if (l6 > 30) {
				l6 = 30;
			}

			int j7 = k1;

			for (int j4 = 0; l6 > 0; j4 += 20) {
				int k4 = Math.min(l6, 10);
				l6 -= k4;

				for (int l4 = 0; l4 < k4; ++l4) {
					byte b2 = 52;
					byte b3 = 0;

					if (flag1) {
						b3 = 1;
					}

					int i5 = j1 - l4 * 8 - 9;
					this.drawTexturedModalRect(i5, j7, b2 + b3 * 9, 9, 9, 9);

					if (l4 * 2 + 1 + j4 < l7) {
						this.drawTexturedModalRect(i5, j7, b2 + 36, 9, 9, 9);
					}

					if (l4 * 2 + 1 + j4 == l7) {
						this.drawTexturedModalRect(i5, j7, b2 + 45, 9, 9, 9);
					}
				}

				j7 -= 10;
			}
		}

		Profiler.in.endStartSection("air");

		if (entityplayer.isInsideOfMaterial(Material.water)) {
			int i6 = this.mc.thePlayer.getAir();
			int j8 = MathHelper.ceiling_double_int((double) (i6 - 2) * 10.0D / 300.0D);
			int k6 = MathHelper.ceiling_double_int((double) i6 * 10.0D / 300.0D) - j8;

			for (int i7 = 0; i7 < j8 + k6; ++i7) {
				if (i7 < j8) {
					this.drawTexturedModalRect(j1 - i7 * 8 - 9, j2, 16, 18, 9, 9);
				} else {
					this.drawTexturedModalRect(j1 - i7 * 8 - 9, j2, 25, 18, 9, 9);
				}
			}
		}

		Profiler.in.endSection();
	}

	/**
	 * Renders dragon's (boss) health on the HUD
	 */
	private void renderBossHealth() {
		Profiler.in.startSection("bossHealth");
		if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
			--BossStatus.statusBarTime;
			AssetsFontRenderer fontrenderer = this.mc.fontRendererObj;
			ScaledResolution scaledresolution = new ScaledResolution(this.mc);
			int i = scaledresolution.getScaledWidth();
			short short1 = 182;
			int j = i / 2 - short1 / 2;
			int k = (int) (BossStatus.healthScale * (float) (short1 + 1));
			byte b0 = 12;
			this.drawTexturedModalRect(j, b0, 0, 74, short1, 5);
			this.drawTexturedModalRect(j, b0, 0, 74, short1, 5);
			if (k > 0) this.drawTexturedModalRect(j, b0, 0, 79, k, 5);

			String s = BossStatus.bossName;
			int l = 0xffffff;

			if (Config.isCustomColors()) l = CustomColors.getBossTextColor(l);

			this.getFontRenderer().drawStringWithShadow(s, (float) (i / 2 - this.getFontRenderer().getStringWidth(s) / 2), (float) (b0 - 10), l);
			G.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(icons);
		}
		Profiler.in.endSection();
	}

	private void renderPumpkinOverlay(ScaledResolution p_180476_1_) {
		ItemStack itemstack = this.mc.thePlayer.inventory.armorItemInSlot(3);
		if (Settings.getPerspective() != 0 || itemstack == null || itemstack.getItem() != Item.getItemFromBlock(Blocks.pumpkin)) return;

		G.disableDepth();
		G.depthMask(false);
		G.tryBlendFuncSeparate(770, 771, 1, 0);
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		G.disableAlpha();
		this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(0.0D, (double) p_180476_1_.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
		worldrenderer.pos((double) p_180476_1_.getScaledWidth(), (double) p_180476_1_.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
		worldrenderer.pos((double) p_180476_1_.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
		worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
		tessellator.draw();
		G.depthMask(true);
		G.enableDepth();
		G.enableAlpha();
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	/**
	 * Renders a Vignette arount the entire screen that changes with light level.
	 */
	private void renderVignette(float brightness, ScaledResolution resolution) {
		if (!Config.isVignetteEnabled()) {
			G.enableDepth();
			G.tryBlendFuncSeparate(770, 771, 1, 0);
		} else {
			brightness = 1.0F - brightness;
			brightness = MathHelper.clamp_float(brightness, 0.0F, 1.0F);
			WorldBorder worldborder = this.mc.theWorld.getWorldBorder();
			float f = (float) worldborder.getClosestDistance(this.mc.thePlayer);
			double d0 = Math.min(worldborder.getResizeSpeed() * (double) worldborder.getWarningTime() * 1000.0D, Math.abs(worldborder.getTargetSize() - worldborder.getDiameter()));
			double d1 = Math.max((double) worldborder.getWarningDistance(), d0);

			if ((double) f < d1) f = 1.0F - (float) ((double) f / d1);
			else f = 0.0F;

			this.prevVignetteBrightness = (float) ((double) this.prevVignetteBrightness + (double) (brightness - this.prevVignetteBrightness) * 0.01D);
			G.disableDepth();
			G.depthMask(false);
			G.tryBlendFuncSeparate(0, 769, 1, 0);

			if (f > 0.0F) G.color(0.0F, f, f, 1.0F);
			else G.color(prevVignetteBrightness, prevVignetteBrightness, prevVignetteBrightness, 1.0F);

			this.mc.getTextureManager().bindTexture(vignetteTexPath);
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldrenderer = tessellator.getWorldRenderer();
			worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
			worldrenderer.pos(0.0D, (double) resolution.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
			worldrenderer.pos((double) resolution.getScaledWidth(), (double) resolution.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
			worldrenderer.pos((double) resolution.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
			worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
			tessellator.draw();
			G.depthMask(true);
			G.enableDepth();
			G.color(1.0F, 1.0F, 1.0F, 1.0F);
			G.tryBlendFuncSeparate(770, 771, 1, 0);
		}
	}

	private void func_180474_b(float p_180474_1_, ScaledResolution p_180474_2_) {
		if (p_180474_1_ < 1.0F) {
			p_180474_1_ = p_180474_1_ * p_180474_1_;
			p_180474_1_ = p_180474_1_ * p_180474_1_;
			p_180474_1_ = p_180474_1_ * 0.8F + 0.2F;
		}

		G.disableAlpha();
		G.disableDepth();
		G.depthMask(false);
		G.tryBlendFuncSeparate(770, 771, 1, 0);
		G.color(1.0F, 1.0F, 1.0F, p_180474_1_);
		this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		TextureAtlasSprite textureatlassprite = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.portal.getDefaultState());
		float f = textureatlassprite.getMinU();
		float f1 = textureatlassprite.getMinV();
		float f2 = textureatlassprite.getMaxU();
		float f3 = textureatlassprite.getMaxV();
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(0.0D, (double) p_180474_2_.getScaledHeight(), -90.0D).tex((double) f, (double) f3).endVertex();
		worldrenderer.pos((double) p_180474_2_.getScaledWidth(), (double) p_180474_2_.getScaledHeight(), -90.0D).tex((double) f2, (double) f3).endVertex();
		worldrenderer.pos((double) p_180474_2_.getScaledWidth(), 0.0D, -90.0D).tex((double) f2, (double) f1).endVertex();
		worldrenderer.pos(0.0D, 0.0D, -90.0D).tex((double) f, (double) f1).endVertex();
		tessellator.draw();
		G.depthMask(true);
		G.enableDepth();
		G.enableAlpha();
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer p_175184_5_) {
		ItemStack itemstack = p_175184_5_.inventory.mainInventory[index];

		if (itemstack != null) {
			float f = (float) itemstack.animationsToGo - partialTicks;

			if (f > 0.0F) {
				G.pushMatrix();
				float f1 = 1.0F + f / 5.0F;
				G.translate((float) (xPos + 8), (float) (yPos + 12), 0.0F);
				G.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
				G.translate((float) -(xPos + 8), (float) -(yPos + 12), 0.0F);
			}

			this.itemRenderer.renderItemAndEffectIntoGUI(itemstack, xPos, yPos);

			if (f > 0.0F) {
				G.popMatrix();
			}

			this.itemRenderer.renderItemOverlays(this.mc.fontRendererObj, itemstack, xPos, yPos);
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
			} else if (this.highlightingItemStack != null && itemstack.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(itemstack, this.highlightingItemStack) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == this.highlightingItemStack.getMetadata())) {
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
		return this.mc.fontRendererObj;
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
