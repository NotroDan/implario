package net.minecraft.client.gui.ingame;

import net.minecraft.block.material.Material;
import net.minecraft.client.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
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


	private int playerHealth = 0;
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
		int i = MathHelper.ceiling_float_int(entityplayer.getHealth());
		int updateCounter = gui.getUpdateCounter();
		boolean flag = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3L % 2L == 1L;

		if (i < playerHealth && entityplayer.hurtResistantTime > 0) {
			lastSystemTime = Minecraft.getSystemTime();
			healthUpdateCounter = updateCounter + 20;
		} else if (i > playerHealth && entityplayer.hurtResistantTime > 0) {
			lastSystemTime = Minecraft.getSystemTime();
			healthUpdateCounter = updateCounter + 10;
		}

		if (Minecraft.getSystemTime() - lastSystemTime > 1000L) {
			playerHealth = i;
			lastPlayerHealth = i;
			lastSystemTime = Minecraft.getSystemTime();
		}

		playerHealth = i;
		int j = lastPlayerHealth;
		rand.setSeed((long) (updateCounter * 312871));
		boolean flag1 = false;
		FoodStats foodstats = entityplayer.getFoodStats();
		int k = foodstats.getFoodLevel();
		int l = foodstats.getPrevFoodLevel();
		IAttributeInstance iattributeinstance = entityplayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
		int i1 = res.getScaledWidth() / 2 - 91;
		int j1 = res.getScaledWidth() / 2 + 91;
		int k1 = res.getScaledHeight() - 39;
		float f = (float) iattributeinstance.getAttributeValue();
		float f1 = entityplayer.getAbsorptionAmount();
		int l1 = MathHelper.ceiling_float_int((f + f1) / 2.0F / 10.0F);
		int i2 = Math.max(10 - (l1 - 2), 3);
		int j2 = k1 - (l1 - 1) * i2 - 10;
		float f2 = f1;
		int k2 = entityplayer.getTotalArmorValue();
		int l2 = -1;

		MC.bindTexture(Gui.icons);

		if (entityplayer.isPotionActive(Potion.regeneration)) {
			l2 = updateCounter % MathHelper.ceiling_float_int(f + 5.0F);
		}

		Profiler.in.startSection("armor");

		for (int i3 = 0; i3 < 10; ++i3) {
			if (k2 > 0) {
				int j3 = i1 + i3 * 8;

				if (i3 * 2 + 1 < k2) {
					gui.drawTexturedModalRect(j3, j2, 34, 9, 9, 9);
				}

				if (i3 * 2 + 1 == k2) {
					gui.drawTexturedModalRect(j3, j2, 25, 9, 9, 9);
				}

				if (i3 * 2 + 1 > k2) {
					gui.drawTexturedModalRect(j3, j2, 16, 9, 9, 9);
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
				i4 += rand.nextInt(2);
			}

			if (j5 == l2) {
				i4 -= 2;
			}

			byte b1 = 0;

			if (entityplayer.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
				b1 = 5;
			}

			gui.drawTexturedModalRect(l3, i4, 16 + b0 * 9, 9 * b1, 9, 9);

			if (flag) {
				if (j5 * 2 + 1 < j) {
					gui.drawTexturedModalRect(l3, i4, k5 + 54, 9 * b1, 9, 9);
				}

				if (j5 * 2 + 1 == j) {
					gui.drawTexturedModalRect(l3, i4, k5 + 63, 9 * b1, 9, 9);
				}
			}

			if (f2 <= 0.0F) {
				if (j5 * 2 + 1 < i) {
					gui.drawTexturedModalRect(l3, i4, k5 + 36, 9 * b1, 9, 9);
				}

				if (j5 * 2 + 1 == i) {
					gui.drawTexturedModalRect(l3, i4, k5 + 45, 9 * b1, 9, 9);
				}
			} else {
				if (f2 == f1 && f1 % 2.0F == 1.0F) {
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
				int i8 = k1;
				int j6 = 16;
				byte b4 = 0;

				if (entityplayer.isPotionActive(Potion.hunger)) {
					j6 += 36;
					b4 = 13;
				}

				if (entityplayer.getFoodStats().getSaturationLevel() <= 0.0F && updateCounter % (k * 3 + 1) == 0) {
					i8 = k1 + rand.nextInt(3) - 1;
				}

				if (flag1) {
					b4 = 1;
				}

				int k7 = j1 - l5 * 8 - 9;
				gui.drawTexturedModalRect(k7, i8, 16 + b4 * 9, 27, 9, 9);

				if (flag1) {
					if (l5 * 2 + 1 < l) {
						gui.drawTexturedModalRect(k7, i8, j6 + 54, 27, 9, 9);
					}

					if (l5 * 2 + 1 == l) {
						gui.drawTexturedModalRect(k7, i8, j6 + 63, 27, 9, 9);
					}
				}

				if (l5 * 2 + 1 < k) {
					gui.drawTexturedModalRect(k7, i8, j6 + 36, 27, 9, 9);
				}

				if (l5 * 2 + 1 == k) {
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
					gui.drawTexturedModalRect(j1 - i7 * 8 - 9, j2, 16, 18, 9, 9);
				} else {
					gui.drawTexturedModalRect(j1 - i7 * 8 - 9, j2, 25, 18, 9, 9);
				}
			}
		}

		Profiler.in.endSection();
	}


}
