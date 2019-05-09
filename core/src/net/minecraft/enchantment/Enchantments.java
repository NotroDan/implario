package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class Enchantments {

	public static final Enchantment
			protection = new EnchantmentProtection(0, new ResourceLocation("protection"), 10, 0),
			fireProtection = new EnchantmentProtection(1, new ResourceLocation("fire_protection"), 5, 1),
			featherFalling = new EnchantmentProtection(2, new ResourceLocation("feather_falling"), 5, 2),
			blastProtection = new EnchantmentProtection(3, new ResourceLocation("blast_protection"), 2, 3),
			projectileProtection = new EnchantmentProtection(4, new ResourceLocation("projectile_protection"), 5, 4),
			respiration = new EnchantmentOxygen(5, new ResourceLocation("respiration"), 2),
			aquaAffinity = new EnchantmentWaterWorker(6, new ResourceLocation("aqua_affinity"), 2),
			thorns = new EnchantmentThorns(7, new ResourceLocation("thorns"), 1),
			depthStrider = new EnchantmentWaterWalker(8, new ResourceLocation("depth_strider"), 2),
			sharpness = new EnchantmentDamage(16, new ResourceLocation("sharpness"), 10, 0),
			smite = new EnchantmentDamage(17, new ResourceLocation("smite"), 5, 1),
			baneOfArthropods = new EnchantmentDamage(18, new ResourceLocation("bane_of_arthropods"), 5, 2),
			knockback = new EnchantmentKnockback(19, new ResourceLocation("knockback"), 5),
			fireAspect = new EnchantmentFireAspect(20, new ResourceLocation("fire_aspect"), 2),
			looting = new EnchantmentLootBonus(21, new ResourceLocation("looting"), 2, EnumEnchantmentType.WEAPON),
			efficiency = new EnchantmentDigging(32, new ResourceLocation("efficiency"), 10),
			silkTouch = new EnchantmentUntouching(33, new ResourceLocation("silk_touch"), 1),
			unbreaking = new EnchantmentDurability(34, new ResourceLocation("unbreaking"), 5),
			fortune = new EnchantmentLootBonus(35, new ResourceLocation("fortune"), 2, EnumEnchantmentType.DIGGER),
			power = new EnchantmentArrowDamage(48, new ResourceLocation("power"), 10),
			punch = new EnchantmentArrowKnockback(49, new ResourceLocation("punch"), 2),
			flame = new EnchantmentArrowFire(50, new ResourceLocation("flame"), 2),
			infinity = new EnchantmentArrowInfinite(51, new ResourceLocation("infinity"), 1),
			luckOfTheSea = new EnchantmentLootBonus(61, new ResourceLocation("luck_of_the_sea"), 2, EnumEnchantmentType.FISHING_ROD),
			lure = new EnchantmentFishingSpeed(62, new ResourceLocation("lure"), 2, EnumEnchantmentType.FISHING_ROD);

	public static Enchantment[] getAll() {
		return Enchantment.enchantmentsList;
	}

}
