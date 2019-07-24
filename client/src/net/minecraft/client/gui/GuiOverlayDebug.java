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
import net.minecraft.client.resources.Lang;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.emptyListIterator;

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
		renderDebugInfoLeft();
		renderDebugInfoRight(scaledResolutionIn);
		renderDebugInfoCenter(scaledResolutionIn);
		G.popMatrix();
		Profiler.in.endSection();
	}

	private boolean isReducedDebug() {
		return mc.thePlayer.hasReducedDebug() || Settings.REDUCED_DEBUG_INFO.i() == 1;
	}

	private void renderDebugInfoLeft() {
		List list = call();

		for (int i = 0; i < list.size(); ++i) {
			String s = (String) list.get(i);

			if (!Strings.isNullOrEmpty(s)) {
				int j = fontRenderer.getFontHeight();
				int k = fontRenderer.getStringWidth(s);
				int l = 2 + j * i;
				drawRect(1, l - 1, 2 + k + 1, l + j - 1, -1873784752);
				fontRenderer.drawString(s, 2, l, 14737632);
			}
		}
	}

	private void renderDebugInfoCenter(ScaledResolution resolution) {
		List list = getDebugInfoCenter();

		for (int i = 0; i < list.size(); ++i) {
			String s = (String) list.get(i);

			if (!Strings.isNullOrEmpty(s)) {
				int j = fontRenderer.getFontHeight();
				int k = fontRenderer.getStringWidth(s);
				int l = (resolution.getScaledWidth() >> 1) - (k >> 1);
				int i1 = (resolution.getScaledHeight() >> 1) + 10 + j * i;
				drawRect(l - 1, i1 - 1, l + k + 1, i1 + j - 1, -1873784752);
				fontRenderer.drawString(s, l, i1, 14737632);
			}
		}
	}


	private void renderDebugInfoRight(ScaledResolution resolution) {
		List list = getDebugInfoRight();

		for (int i = 0; i < list.size(); ++i) {
			String s = (String) list.get(i);

			if (!Strings.isNullOrEmpty(s)) {
				int j = fontRenderer.getFontHeight();
				int k = fontRenderer.getStringWidth(s);
				int l = resolution.getScaledWidth() - 2 - k;
				int i1 = 2 + j * i;
				drawRect(l - 1, i1 - 1, l + k + 1, i1 + j - 1, -1873784752);
				fontRenderer.drawString(s, l, i1, 14737632);
			}
		}
	}

	protected List call() {
		if (Settings.REDUCED_DEBUG_INFO.i() == 2) return Lists.newArrayList(mc.debug);
		BlockPos blockpos = new BlockPos(mc.getRenderViewEntity().posX,
				mc.getRenderViewEntity().getEntityBoundingBox().minY, mc.getRenderViewEntity().posZ);
		Entity entity = mc.getRenderViewEntity();
		EnumFacing enumfacing = entity.getHorizontalFacing();
		String s = "§cНеизвестно";

		switch (sides[enumfacing.ordinal()]) {
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

		List<String> list = new ArrayList<>();
		if (isReducedDebug()) {
			list.add("Клиент §dImplario §f(Minecraft 1.8.8)");
			list.add(mc.debug);
		} else {
			list.add("Клиент Implario (1.8.8), §a" + MC.getPlayer().getName());
			list.add(mc.debug);
			list.add(mc.renderGlobal.getDebugInfoRenders());
			list.add(mc.renderGlobal.getDebugInfoEntities());
			list.add("P: " + mc.effectRenderer.getStatistics() + ". T: " + mc.theWorld.getDebugLoadedEntities());
			list.add(mc.theWorld.getProviderName());
			list.add("");
		}


		list.add(String.format("Координаты: §a%.3f §f/ §a%.5f §f/ §a%.3f", mc.getRenderViewEntity().posX,
				mc.getRenderViewEntity().getEntityBoundingBox().minY, mc.getRenderViewEntity().posZ));
		list.add(String.format("Блок: §a%d %d %d", blockpos.getX(), blockpos.getY(), blockpos.getZ()));
		list.add(String.format("Чанк: §a%d %d %d §7§o(локально)§f, §a%d %d %d §7§o(глобально)",
				blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15,
				blockpos.getX() >> 4, blockpos.getY() >> 4, blockpos.getZ() >> 4));
		list.add(String.format("Направление: §a%s §f(§a%s§f) (§a%.1f §f/§a %.1f§f)", enumfacing, s,
				MathHelper.wrapAngleTo180_float(entity.rotationYaw), MathHelper.wrapAngleTo180_float(entity.rotationPitch)));

		if (mc.theWorld != null && mc.theWorld.isBlockLoaded(blockpos)) {
			Chunk chunk = mc.theWorld.getChunkFromBlockCoords(blockpos);
			String biome = chunk.getBiome(blockpos, mc.theWorld.getWorldChunkManager()).getAddress();
			list.add("Биом: §a" + Lang.format(biome));
			list.add("Свет: §a" + chunk.getLightSubtracted(blockpos, 0) + " §7§o("
					+ chunk.getLightFor(EnumSkyBlock.SKY, blockpos) + " от неба, " +
					chunk.getLightFor(EnumSkyBlock.BLOCK, blockpos) + " от блоков)");

			if (!isReducedDebug()) {
				DifficultyInstance difficultyinstance = mc.theWorld.getDifficultyForLocation(blockpos);
				if (mc.isIntegratedServerRunning() && mc.getIntegratedServer() != null) {
					EntityPlayerMP entityplayermp = mc.getIntegratedServer().getConfigurationManager()
							.getPlayerByUUID(mc.thePlayer.getUniqueID());
					if (entityplayermp != null)
						difficultyinstance = entityplayermp.worldObj.getDifficultyForLocation(new BlockPos(entityplayermp));
				}
				list.add(String.format("Local Difficulty: %.2f (Day %d)",
						difficultyinstance.getAdditionalDifficulty(),
						mc.theWorld.getWorldTime() / 24000L));
			}
		}

		if (mc.entityRenderer != null && mc.entityRenderer.isShaderActive())
			list.add("Шейдер: §a" + mc.entityRenderer.getShaderGroup().getShaderGroupName());

		return list;
	}

	@SuppressWarnings ("unchecked")
	private List<String> getDebugInfoCenter() {
		if (Settings.REDUCED_DEBUG_INFO.i() == 2) return new ArrayList<>();
		if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
				&& mc.objectMouseOver.getBlockPos() != null) {
			List<String> list = new ArrayList<>();
			BlockPos blockpos1 = mc.objectMouseOver.getBlockPos();
			list.add("§e" + blockpos1.getX() + " " + blockpos1.getY() + " " + blockpos1.getZ());
			IBlockState iblockstate = mc.theWorld.getBlockState(blockpos1);

			if (mc.theWorld.getWorldType() != WorldType.DEBUG)
				iblockstate = iblockstate.getBlock().getActualState(iblockstate, mc.theWorld, blockpos1);
			Block b = iblockstate.getBlock();
			RegistryNamespacedDefaultedByKey<ResourceLocation, Block> r = Block.blockRegistry;
			list.add(r.getNameForObject(b) + " §e" + r.getIDForObject(b) + "§f:§e" + b.getMetaFromState(iblockstate));

			if (isReducedDebug()) return list;
			for (Map.Entry<IProperty, Comparable> e : iblockstate.getProperties().entrySet()) {
				String value = e.getValue().toString();
				if (e.getValue() == Boolean.TRUE) value = "§a" + value;
				if (e.getValue() == Boolean.FALSE) value = "§c" + value;
				list.add(e.getKey().getName() + ": " + value);
			}
			return list;
		}
		return (List<String>) Collections.EMPTY_LIST;
	}

	private List<String> getDebugInfoRight() {
		supportFunc();
		if (Settings.REDUCED_DEBUG_INFO.i() == 2) return new ArrayList<>();
		long i = Runtime.getRuntime().maxMemory();
		long j = Runtime.getRuntime().totalMemory();
		long k = Runtime.getRuntime().freeMemory();
		long l = j - k;
		if (isReducedDebug()) {
			List<String> list = new ArrayList<>();
			list.add("Версия Java: §a" + System.getProperty("java.version") + "§fx" + (this.mc.isJava64bit() ? 64 : 32));
			list.add("Память: §a" + l * 100L / i + "% " + bytesToMb(l) + " §f/§a " + bytesToMb(i) + " §fМБ");
			list.add("Выделено: §a" + j * 100L / i + "% " + bytesToMb(j) + " §fМБ");
			return list;
		}

		List<String> list = new ArrayList<>();
		list.add(String.format("Версия Java: §a%s§fx%d", System.getProperty("java.version"), this.mc.isJava64bit() ? 64 : 32));
		list.add(String.format("Память:§a% 2d%% %03d§f/§a%03d§f МБ", l * 100L / i, bytesToMb(l), bytesToMb(i)));
		list.add(String.format("Выделено:§a% 2d%% %03d§f МБ", j * 100L / i, bytesToMb(j)));
		list.add("");
		list.add("Процессор: §a" + OpenGlHelper.getCPU());
		list.add(String.format("Видеокарта: §a%d§fx§a%d §f(§a%s§f)", Display.getWidth(),
				Display.getHeight(), GL11.glGetString(GL11.GL_VENDOR)));
		list.add("GL-Рендерер: §a" + GL11.glGetString(GL11.GL_RENDERER));
		list.add("Версия GL: §a" + GL11.glGetString(GL11.GL_VERSION));
		return list;
	}

	private void supportFunc() {
		G.disableDepth();
		FrameTimer frametimer = mc.func_181539_aj();
		int i = frametimer.func_181749_a();
		int j = frametimer.func_181750_b();
		long[] along = frametimer.func_181746_c();
		ScaledResolution scaledresolution = new ScaledResolution(mc);
		int k = i;
		int l = 0;
		drawRect(0, scaledresolution.getScaledHeight() - 60, 240, scaledresolution.getScaledHeight(), -1873784752);

		while (k != j) {
			int i1 = frametimer.func_181748_a(along[k], 30);
			int j1 = mathSupport(MathHelper.clamp_int(i1, 0, 60));
			drawVerticalLine(l, scaledresolution.getScaledHeight(), scaledresolution.getScaledHeight() - i1, j1);
			++l;
			k = frametimer.func_181751_b(k + 1);
		}

		drawRect(1, scaledresolution.getScaledHeight() - 29, 14,
				scaledresolution.getScaledHeight() - 20, -1873784752);
		fontRenderer.drawString("60", 2, scaledresolution.getScaledHeight() - 28, 14737632);
		drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 30, -1);
		drawRect(1, scaledresolution.getScaledHeight() - 59, 14,
				scaledresolution.getScaledHeight() - 50, -1873784752);
		fontRenderer.drawString("30", 2, scaledresolution.getScaledHeight() - 58, 14737632);
		drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 60, -1);
		drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 1, -1);
		drawVerticalLine(0, scaledresolution.getScaledHeight() - 60, scaledresolution.getScaledHeight(), -1);
		drawVerticalLine(239, scaledresolution.getScaledHeight() - 60, scaledresolution.getScaledHeight(), -1);

		if (Settings.FRAMERATE_LIMIT.f() <= 120)
			drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 60 +
					((int) Settings.FRAMERATE_LIMIT.f() >> 1), -16711681);

		G.enableDepth();
	}

	private int mathSupport(int i) {
		return i < 30 ? mathSupportTwo(-16711936, -256, (float) i / (float) 30)
				: mathSupportTwo(-256, -65536, (float) (i - 30) / (float) (30));
	}

	private int mathSupportTwo(int one, int two, float three) {
		int i = one >> 24 & 255;
		int j = one >> 16 & 255;
		int k = one >> 8 & 255;
		int l = one & 255;
		int i1 = two >> 24 & 255;
		int j1 = two >> 16 & 255;
		int k1 = two >> 8 & 255;
		int l1 = two & 255;
		int i2 = MathHelper.clamp_int((int) ((float) i + (float) (i1 - i) * three), 0, 255);
		int j2 = MathHelper.clamp_int((int) ((float) j + (float) (j1 - j) * three), 0, 255);
		int k2 = MathHelper.clamp_int((int) ((float) k + (float) (k1 - k) * three), 0, 255);
		int l2 = MathHelper.clamp_int((int) ((float) l + (float) (l1 - l) * three), 0, 255);
		return i2 << 24 | j2 << 16 | k2 << 8 | l2;
	}

	private static long bytesToMb(long bytes) {
		return bytes >> 20L;
	}

	private static final int[] sides = new int[EnumFacing.values().length];

	static {
		try {
			sides[EnumFacing.NORTH.ordinal()] = 1;
		} catch (NoSuchFieldError ignored) {}

		try {
			sides[EnumFacing.SOUTH.ordinal()] = 2;
		} catch (NoSuchFieldError ignored) {}

		try {
			sides[EnumFacing.WEST.ordinal()] = 3;
		} catch (NoSuchFieldError ignored) {}

		try {
			sides[EnumFacing.EAST.ordinal()] = 4;
		} catch (NoSuchFieldError ignored) {}
	}
}
