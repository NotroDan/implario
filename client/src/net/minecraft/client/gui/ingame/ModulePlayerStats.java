package net.minecraft.client.gui.ingame;

import net.minecraft.block.material.Material;
import net.minecraft.client.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.element.Colors;
import net.minecraft.client.renderer.G;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.potion.Potion;
import net.minecraft.server.Profiler;
import net.minecraft.util.FoodStats;
import net.minecraft.util.MathHelper;

import java.util.Random;

public class ModulePlayerStats implements Module {


	private int prevHealth = 0;
	private int lastPlayerHealth = 0;
	/**
	 * Used with updateCounter to make the heart bar flash
	 */
	private long healthUpdateCounter = 0L;
	/**
	 * The last recorded system time
	 */
	private long lastSystemTime = 0L;
	private final Random rand = new Random();


	@Override
	public void render(GuiIngame gui, float partialTicks, ScaledResolution res) {
		Minecraft mc = MC.i();
		if (!mc.playerController.shouldDrawHUD()) return;
		if (!(mc.getRenderViewEntity() instanceof EntityPlayer)) return;
		EntityPlayer entityplayer = (EntityPlayer) mc.getRenderViewEntity();
		int health = MathHelper.ceiling_float_int(entityplayer.getHealth());
		int updateCounter = gui.getUpdateCounter();
		boolean flag = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3L % 2L == 1L;

		if (entityplayer.hurtResistantTime > 0 && health != prevHealth) {
			lastSystemTime = Minecraft.getSystemTime();
			healthUpdateCounter = updateCounter + (health < prevHealth ? 20 : 10);
		}

		if (Minecraft.getSystemTime() - lastSystemTime > 1000L) {
			prevHealth = health;
			lastPlayerHealth = health;
			lastSystemTime = Minecraft.getSystemTime();
		}

		prevHealth = health;
		int j = lastPlayerHealth;
		rand.setSeed(updateCounter * 312871);
		boolean flag1 = false;
		FoodStats foodstats = entityplayer.getFoodStats();
		int iFood = foodstats.getFoodLevel();
		int prevIFood = foodstats.getPrevFoodLevel();
		IAttributeInstance maxHealthAttrib = entityplayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
		int leftBorder = res.getScaledWidth() / 2 - 91;
		int x2 = res.getScaledWidth() / 2 + 91;
		int y1 = res.getScaledHeight() - 39;
		float maxHealth = (float) maxHealthAttrib.getAttributeValue();
		float absorption = entityplayer.getAbsorptionAmount();
		int iAbsorption = MathHelper.ceiling_float_int((maxHealth + absorption) / 20.0F);
		int i2 = Math.max(10 - (iAbsorption - 2), 3);
		int j2 = y1 - (iAbsorption - 1) * i2 - 10;
		float f2 = absorption;
		int iArmor = entityplayer.getTotalArmorValue();

		MC.bindTexture(Gui.icons);

		// Броня
		Profiler.in.startSection("armor");
		if (iArmor > 0) for (int i = 0; i < 10; ++i) {
			int x = leftBorder + i * 8;
			int armor = i * 2 + 1;
			// Координаты текстуры: x=34 для полной, x=25 для половинки, x=16 для пустой
			int tx = armor < iArmor ? 34 : armor == iArmor ? 25 : 16;
			gui.drawTexturedModalRect(x, j2, tx, 9, 9, 9);
		}
		Profiler.in.endSection();


		int regenerationWobble = -1;
		if (entityplayer.isPotionActive(Potion.regeneration)) {
			regenerationWobble = updateCounter % MathHelper.ceiling_float_int(maxHealth + 5.0F);
		}


		// Здоровье
		Profiler.in.startSection("health");

		boolean wide = false;
		int hm = wide ? 2 : 1; // Множитель высоты

		int y2 = y1 + 15;
		int elements = (int) maxHealth + (int) absorption;
		int height = MathHelper.ceiling_float_int(elements / 20.0F) * 8;
		if (wide) height = height * 2 - 2;
		Gui.drawRect(leftBorder, y2 - height, leftBorder + 84, y2 + 1, Colors.DARK);
		Gui.drawRect(leftBorder + 71, y2 - 8 * hm + 1, leftBorder + 83, y2, Colors.GRAY);
		for (int i = 0; i < elements; i++) {
			boolean odd = (i & 1) != 0;
			int color = i >= maxHealth ? Colors.YELLOW : i < health ? Colors.RED : Colors.GRAY;
			int heart = i % 20;
			int x = (heart >> 1) * 7;
			int y = (odd ? 1 + 3 * hm : 0) + i / 20 * 8 * hm;
			Gui.drawRect(leftBorder + 1 + x, y2 - y - 3 * hm, leftBorder + 1 + x + 6, y2 - y, color);
		}
		String s = String.valueOf(health + (int) absorption);
		MC.FR.drawString(s, leftBorder + 77 - MC.FR.getStringWidth(s) / 2, y2 - (wide ? 14 : 8), absorption == 0 ? Colors.RED : Colors.YELLOW);
		if (wide) MC.FR.drawString("HP", (leftBorder + 74), (y2 - 8), Colors.RED);


		MC.bindTexture(Gui.icons);

		G.color(1, 1, 1, 1);
		if (false) for (int i = MathHelper.ceiling_float_int((maxHealth + absorption) / 2.0F) - 1; i >= 0; --i) {
			int k5 = 16;

			if (entityplayer.isPotionActive(Potion.poison)) k5 += 36;
			else if (entityplayer.isPotionActive(Potion.wither)) k5 += 72;

			byte b0 = 0;

			if (flag) {
				b0 = 1;
			}

			int k3 = MathHelper.ceiling_float_int((float) (i + 1) / 10.0F) - 1;
			int l3 = leftBorder + i % 10 * 8;
			int i4 = y1 - k3 * i2;

			if (health <= 4) {
				i4 += rand.nextInt(2);
			}

			if (i == regenerationWobble) {
				i4 -= 2;
			}

			byte b1 = 0;

			if (entityplayer.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
				b1 = 5;
			}

			gui.drawTexturedModalRect(l3, i4, 16 + b0 * 9, 9 * b1, 9, 9);

			if (flag) {
				if (i * 2 + 1 < j) {
					gui.drawTexturedModalRect(l3, i4, k5 + 54, 9 * b1, 9, 9);
				}

				if (i * 2 + 1 == j) {
					gui.drawTexturedModalRect(l3, i4, k5 + 63, 9 * b1, 9, 9);
				}
			}

			if (f2 <= 0.0F) {
				if (i * 2 + 1 < health) {
					gui.drawTexturedModalRect(l3, i4, k5 + 36, 9 * b1, 9, 9);
				}

				if (i * 2 + 1 == health) {
					gui.drawTexturedModalRect(l3, i4, k5 + 45, 9 * b1, 9, 9);
				}
			} else {
				if (f2 == absorption && absorption % 2.0F == 1.0F) {
					gui.drawTexturedModalRect(l3, i4, k5 + 153, 9 * b1, 9, 9);
				} else {
					gui.drawTexturedModalRect(l3, i4, k5 + 144, 9 * b1, 9, 9);
				}

				f2 -= 2.0F;
			}
		}

		Entity entity = entityplayer.ridingEntity;

		if (entity == null) {
			Profiler.in.endStartSection("food");

			for (int l5 = 0; l5 < 10; ++l5) {
				int i8 = y1;
				int j6 = 16;
				byte b4 = 0;

				if (entityplayer.isPotionActive(Potion.hunger)) {
					j6 += 36;
					b4 = 13;
				}

				if (entityplayer.getFoodStats().getSaturationLevel() <= 0.0F && updateCounter % (iFood * 3 + 1) == 0) {
					i8 = y1 + rand.nextInt(3) - 1;
				}

				if (flag1) {
					b4 = 1;
				}

				int k7 = x2 - l5 * 8 - 9;
				gui.drawTexturedModalRect(k7, i8, 16 + b4 * 9, 27, 9, 9);

				if (flag1) {
					if (l5 * 2 + 1 < prevIFood) {
						gui.drawTexturedModalRect(k7, i8, j6 + 54, 27, 9, 9);
					}

					if (l5 * 2 + 1 == prevIFood) {
						gui.drawTexturedModalRect(k7, i8, j6 + 63, 27, 9, 9);
					}
				}

				if (l5 * 2 + 1 < iFood) {
					gui.drawTexturedModalRect(k7, i8, j6 + 36, 27, 9, 9);
				}

				if (l5 * 2 + 1 == iFood) {
					gui.drawTexturedModalRect(k7, i8, j6 + 45, 27, 9, 9);
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

			int j7 = y1;

			for (int j4 = 0; l6 > 0; j4 += 20) {
				int k4 = Math.min(l6, 10);
				l6 -= k4;

				for (int l4 = 0; l4 < k4; ++l4) {
					byte b2 = 52;
					byte b3 = 0;

					if (flag1) {
						b3 = 1;
					}

					int i5 = x2 - l4 * 8 - 9;
					gui.drawTexturedModalRect(i5, j7, b2 + b3 * 9, 9, 9, 9);

					if (l4 * 2 + 1 + j4 < l7) {
						gui.drawTexturedModalRect(i5, j7, b2 + 36, 9, 9, 9);
					}

					if (l4 * 2 + 1 + j4 == l7) {
						gui.drawTexturedModalRect(i5, j7, b2 + 45, 9, 9, 9);
					}
				}

				j7 -= 10;
			}
		}

		Profiler.in.endStartSection("air");

		if (entityplayer.isInsideOfMaterial(Material.water)) {
			int i6 = mc.thePlayer.getAir();
			int j8 = MathHelper.ceiling_double_int((double) (i6 - 2) * 10.0D / 300.0D);
			int k6 = MathHelper.ceiling_double_int((double) i6 * 10.0D / 300.0D) - j8;

			for (int i7 = 0; i7 < j8 + k6; ++i7) {
				if (i7 < j8) {
					gui.drawTexturedModalRect(x2 - i7 * 8 - 9, j2, 16, 18, 9, 9);
				} else {
					gui.drawTexturedModalRect(x2 - i7 * 8 - 9, j2, 25, 18, 9, 9);
				}
			}
		}

		Profiler.in.endSection();
	}


}
