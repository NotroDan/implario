package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.AssetsFontRenderer;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.settings.Settings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.Profiler;
import net.minecraft.util.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuiOverlayDebug extends Gui {

	private final Minecraft mc;
	private final AssetsFontRenderer fontRenderer;

	public GuiOverlayDebug(Minecraft mc) {
		this.mc = mc;
		this.fontRenderer = mc.fontRenderer;
	}

	public void renderDebugInfo(ScaledResolution scaledResolutionIn) {
		if (!Settings.SHOW_DEBUG.b()) return;
		Profiler.in.startSection("debug");
		G.pushMatrix();
		this.renderDebugInfoLeft();
		this.renderDebugInfoRight(scaledResolutionIn);
		this.renderDebugInfoCenter(scaledResolutionIn);
		G.popMatrix();
		Profiler.in.endSection();
	}

	private boolean isReducedDebug() {
		return this.mc.thePlayer.hasReducedDebug() || Settings.REDUCED_DEBUG_INFO.i() == 1;
	}

	protected void renderDebugInfoLeft() {
		List list = this.call();

		for (int i = 0; i < list.size(); ++i) {
			String s = (String) list.get(i);

			if (!Strings.isNullOrEmpty(s)) {
				int j = this.fontRenderer.getFontHeight();
				int k = this.fontRenderer.getStringWidth(s);
				boolean flag = true;
				int l = 2 + j * i;
				drawRect(1, l - 1, 2 + k + 1, l + j - 1, -1873784752);
				this.fontRenderer.drawString(s, 2, l, 14737632);
			}
		}
	}

	private void renderDebugInfoCenter(ScaledResolution resolution) {
		List list = this.getDebugInfoCenter();

		for (int i = 0; i < list.size(); ++i) {
			String s = (String) list.get(i);

			if (!Strings.isNullOrEmpty(s)) {
				int j = this.fontRenderer.getFontHeight();
				int k = this.fontRenderer.getStringWidth(s);
				int l = resolution.getScaledWidth() / 2 - k / 2;
				int i1 = resolution.getScaledHeight() / 2 + 10 + j * i;
				drawRect(l - 1, i1 - 1, l + k + 1, i1 + j - 1, -1873784752);
				this.fontRenderer.drawString(s, l, i1, 14737632);
			}
		}
	}


	protected void renderDebugInfoRight(ScaledResolution resolution) {
		List list = this.getDebugInfoRight();

		for (int i = 0; i < list.size(); ++i) {
			String s = (String) list.get(i);

			if (!Strings.isNullOrEmpty(s)) {
				int j = this.fontRenderer.getFontHeight();
				int k = this.fontRenderer.getStringWidth(s);
				int l = resolution.getScaledWidth() - 2 - k;
				int i1 = 2 + j * i;
				drawRect(l - 1, i1 - 1, l + k + 1, i1 + j - 1, -1873784752);
				this.fontRenderer.drawString(s, l, i1, 14737632);
			}
		}
	}

	protected List call() {
		if (Settings.REDUCED_DEBUG_INFO.i() == 2) return Lists.newArrayList(this.mc.debug);
		BlockPos blockpos = new BlockPos(this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ);
		Entity entity = this.mc.getRenderViewEntity();
		EnumFacing enumfacing = entity.getHorizontalFacing();
		String s = "§cНеизвестно";

		switch (GuiOverlayDebug.GuiOverlayDebug$1.field_178907_a[enumfacing.ordinal()]) {
			case 1:
				s = "Уменьшение Z";
				break;
			case 2:
				s = "Увеличение Z";
				break;
			case 3:
				s = "Уменьшение X";
				break;
			case 4:
				s = "Увеличение X";
				break;
		}

		ArrayList arraylist;
		if (isReducedDebug())
			arraylist = Lists.newArrayList("Клиент §dImplario §f(Minecraft 1.8.8)", mc.debug);
		else
			arraylist = Lists.newArrayList(
					"Клиент Implario (1.8.8), §a" + MC.getPlayer().getName(),
					this.mc.debug,
					this.mc.renderGlobal.getDebugInfoRenders(),
					this.mc.renderGlobal.getDebugInfoEntities(),
					"P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.theWorld.getDebugLoadedEntities(),
					this.mc.theWorld.getProviderName(),
					""
										  );


		arraylist.add(String.format("Координаты: §a%.3f §f/ §a%.5f §f/ §a%.3f", this.mc.getRenderViewEntity().posX,
				this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ));
		arraylist.add(String.format("Блок: §a%d %d %d", blockpos.getX(), blockpos.getY(), blockpos.getZ()));
		arraylist.add(String.format("Чанк: §a%d %d %d §7§o(локально)§f, §a%d %d %d §7§o(глобально)", blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15,
				blockpos.getX() >> 4, blockpos.getY() >> 4, blockpos.getZ() >> 4));
		arraylist.add(String.format("Направление: §a%s §f(§a%s§f) (§a%.1f §f/§a %.1f§f)", enumfacing, s,
				MathHelper.wrapAngleTo180_float(entity.rotationYaw), MathHelper.wrapAngleTo180_float(entity.rotationPitch)));

		if (this.mc.theWorld != null && this.mc.theWorld.isBlockLoaded(blockpos)) {

			Chunk chunk = this.mc.theWorld.getChunkFromBlockCoords(blockpos);
			arraylist.add("Биом: §a" + chunk.getBiome(blockpos, this.mc.theWorld.getWorldChunkManager()).biomeName);
			arraylist.add("Свет: §a" + chunk.getLightSubtracted(blockpos, 0) + " §7§o(" + chunk.getLightFor(EnumSkyBlock.SKY, blockpos) + " от неба, " +
					chunk.getLightFor(EnumSkyBlock.BLOCK, blockpos) + " от блоков)");

			if (!isReducedDebug()) {
				DifficultyInstance difficultyinstance = this.mc.theWorld.getDifficultyForLocation(blockpos);
				if (this.mc.isIntegratedServerRunning() && this.mc.getIntegratedServer() != null) {
					EntityPlayerMP entityplayermp = this.mc.getIntegratedServer().getConfigurationManager().getPlayerByUUID(this.mc.thePlayer.getUniqueID());
					if (entityplayermp != null) difficultyinstance = entityplayermp.worldObj.getDifficultyForLocation(new BlockPos(entityplayermp));
				}
				arraylist.add(String.format("Local Difficulty: %.2f (Day %d)", difficultyinstance.getAdditionalDifficulty(), this.mc.theWorld.getWorldTime() / 24000L));
			}
		}

		if (this.mc.entityRenderer != null && this.mc.entityRenderer.isShaderActive())
			arraylist.add("Шейдер: §a" + this.mc.entityRenderer.getShaderGroup().getShaderGroupName());

		return arraylist;
	}

	protected List getDebugInfoCenter() {
		if (Settings.REDUCED_DEBUG_INFO.i() == 2) return new ArrayList();
		if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.objectMouseOver.getBlockPos() != null) {
			List<String> arraylist = new ArrayList<>();
			BlockPos blockpos1 = this.mc.objectMouseOver.getBlockPos();
			arraylist.add("§e" + blockpos1.getX() + " " + blockpos1.getY() + " " + blockpos1.getZ());
			IBlockState iblockstate = this.mc.theWorld.getBlockState(blockpos1);

			if (this.mc.theWorld.getWorldType() != WorldType.DEBUG_WORLD)
				iblockstate = iblockstate.getBlock().getActualState(iblockstate, this.mc.theWorld, blockpos1);
			Block b = iblockstate.getBlock();
			RegistryNamespacedDefaultedByKey<ResourceLocation, Block> r = Block.blockRegistry;
			arraylist.add(String.valueOf(r.getNameForObject(b)) + " §e" + r.getIDForObject(b) + "§f:§e" + b.getMetaFromState(iblockstate));

			if (isReducedDebug()) return arraylist;
			for (Map.Entry<IProperty, Comparable> e : iblockstate.getProperties().entrySet()) {
				String value = e.getValue().toString();
				if (e.getValue() == Boolean.TRUE) value = "§a" + value;
				if (e.getValue() == Boolean.FALSE) value = "§c" + value;
				arraylist.add(e.getKey().getName() + ": " + value);
			}
			return arraylist;
		}
		return new ArrayList<>();
	}

	protected List getDebugInfoRight() {
		func_181554_e();
		if (Settings.REDUCED_DEBUG_INFO.i() == 2) return new ArrayList();
		long i = Runtime.getRuntime().maxMemory();
		long j = Runtime.getRuntime().totalMemory();
		long k = Runtime.getRuntime().freeMemory();
		long l = j - k;
		if (isReducedDebug()) return Lists.newArrayList(
				"Версия Java: §a" + System.getProperty("java.version") + "§fx" + (this.mc.isJava64bit() ? 64 : 32),
				"Память: §a" + l * 100L / i + "% " + bytesToMb(l) + " §f/§a " + bytesToMb(i) + " §fМБ",
				"Выделено: §a" + j * 100L / i + "% " + bytesToMb(j) + " §fМБ"
													   );

		return Lists.newArrayList(
				String.format("Версия Java: §a%s§fx%d", System.getProperty("java.version"), this.mc.isJava64bit() ? 64 : 32),
				String.format("Память:§a% 2d%% %03d§f/§a%03d§f МБ", l * 100L / i, bytesToMb(l), bytesToMb(i)),
				String.format("Выделено:§a% 2d%% %03d§f МБ", j * 100L / i, bytesToMb(j)),
				"",
				String.format("Процессор: §a%s", OpenGlHelper.getCPU()),
				String.format("Видеокарта: §a%d§fx§a%d §f(§a%s§f)", Display.getWidth(), Display.getHeight(), GL11.glGetString(GL11.GL_VENDOR)),
				"GL-Рендерер: §a" + GL11.glGetString(GL11.GL_RENDERER),
				"Версия GL: §a" + GL11.glGetString(GL11.GL_VERSION));
	}

	private void func_181554_e() {
		G.disableDepth();
		FrameTimer frametimer = this.mc.func_181539_aj();
		int i = frametimer.func_181749_a();
		int j = frametimer.func_181750_b();
		long[] along = frametimer.func_181746_c();
		ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		int k = i;
		int l = 0;
		drawRect(0, scaledresolution.getScaledHeight() - 60, 240, scaledresolution.getScaledHeight(), -1873784752);

		while (k != j) {
			int i1 = frametimer.func_181748_a(along[k], 30);
			int j1 = this.func_181552_c(MathHelper.clamp_int(i1, 0, 60), 0, 30, 60);
			this.drawVerticalLine(l, scaledresolution.getScaledHeight(), scaledresolution.getScaledHeight() - i1, j1);
			++l;
			k = frametimer.func_181751_b(k + 1);
		}

		drawRect(1, scaledresolution.getScaledHeight() - 30 + 1, 14, scaledresolution.getScaledHeight() - 30 + 10, -1873784752);
		this.fontRenderer.drawString("60", 2, scaledresolution.getScaledHeight() - 30 + 2, 14737632);
		this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 30, -1);
		drawRect(1, scaledresolution.getScaledHeight() - 60 + 1, 14, scaledresolution.getScaledHeight() - 60 + 10, -1873784752);
		this.fontRenderer.drawString("30", 2, scaledresolution.getScaledHeight() - 60 + 2, 14737632);
		this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 60, -1);
		this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 1, -1);
		this.drawVerticalLine(0, scaledresolution.getScaledHeight() - 60, scaledresolution.getScaledHeight(), -1);
		this.drawVerticalLine(239, scaledresolution.getScaledHeight() - 60, scaledresolution.getScaledHeight(), -1);

		if (Settings.FRAMERATE_LIMIT.f() <= 120)
			this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 60 + (int) Settings.FRAMERATE_LIMIT.f() / 2, -16711681);

		G.enableDepth();
	}

	private int func_181552_c(int p_181552_1_, int p_181552_2_, int p_181552_3_, int p_181552_4_) {
		return p_181552_1_ < p_181552_3_ ? this.func_181553_a(-16711936, -256, (float) p_181552_1_ / (float) p_181552_3_) : this.func_181553_a(-256, -65536,
				(float) (p_181552_1_ - p_181552_3_) / (float) (p_181552_4_ - p_181552_3_));
	}

	private int func_181553_a(int p_181553_1_, int p_181553_2_, float p_181553_3_) {
		int i = p_181553_1_ >> 24 & 255;
		int j = p_181553_1_ >> 16 & 255;
		int k = p_181553_1_ >> 8 & 255;
		int l = p_181553_1_ & 255;
		int i1 = p_181553_2_ >> 24 & 255;
		int j1 = p_181553_2_ >> 16 & 255;
		int k1 = p_181553_2_ >> 8 & 255;
		int l1 = p_181553_2_ & 255;
		int i2 = MathHelper.clamp_int((int) ((float) i + (float) (i1 - i) * p_181553_3_), 0, 255);
		int j2 = MathHelper.clamp_int((int) ((float) j + (float) (j1 - j) * p_181553_3_), 0, 255);
		int k2 = MathHelper.clamp_int((int) ((float) k + (float) (k1 - k) * p_181553_3_), 0, 255);
		int l2 = MathHelper.clamp_int((int) ((float) l + (float) (l1 - l) * p_181553_3_), 0, 255);
		return i2 << 24 | j2 << 16 | k2 << 8 | l2;
	}

	private static long bytesToMb(long bytes) {
		return bytes / 1024L / 1024L;
	}

	static final class GuiOverlayDebug$1 {

		static final int[] field_178907_a = new int[EnumFacing.values().length];
		static {
			try {
				field_178907_a[EnumFacing.NORTH.ordinal()] = 1;
			} catch (NoSuchFieldError ignored) {}

			try {
				field_178907_a[EnumFacing.SOUTH.ordinal()] = 2;
			} catch (NoSuchFieldError ignored) {}

			try {
				field_178907_a[EnumFacing.WEST.ordinal()] = 3;
			} catch (NoSuchFieldError ignored) {}

			try {
				field_178907_a[EnumFacing.EAST.ordinal()] = 4;
			} catch (NoSuchFieldError ignored) {}
		}
	}

}
