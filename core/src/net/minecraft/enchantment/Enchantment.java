package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.server.Todo;
import net.minecraft.util.*;
import net.minecraft.util.functional.StringUtils;

import java.util.*;

public abstract class Enchantment {

	protected static final Enchantment[] enchantments = new Enchantment[256];
	public static final List<Enchantment> enchantmentList = new ArrayList<>();
	private static final Map<ResourceLocation, Enchantment> locationEnchantments = new HashMap<>();


	public final int effectId;
	private final int weight;

	public EnumEnchantmentType type;

	protected String name;

	public static Enchantment getEnchantmentById(int enchID) {
		return enchID >= 0 && enchID < enchantments.length ? enchantments[enchID] : null;
	}

	protected Enchantment(int enchID, ResourceLocation enchName, int enchWeight, EnumEnchantmentType enchType) {
		this.effectId = enchID;
		this.weight = enchWeight;
		this.type = enchType;

		if (enchantments[enchID] != null)
			throw new IllegalArgumentException("Duplicate enchantment id!");
		enchantments[enchID] = this;
		enchantmentList.add(this);
		locationEnchantments.put(enchName, this);
	}

	public static Enchantment getEnchantmentByLocation(String location) {
		return locationEnchantments.get(new ResourceLocation(location));
	}

	public static Set<ResourceLocation> func_181077_c() {
		return locationEnchantments.keySet();
	}

	public int getWeight() {
		return this.weight;
	}

	public int getMinLevel() {
		return 1;
	}

	public int getMaxLevel() {
		return 1;
	}

	public int getMinEnchantability(int enchantmentLevel) {
		return 1 + enchantmentLevel * 10;
	}

	public int getMaxEnchantability(int enchantmentLevel) {
		return this.getMinEnchantability(enchantmentLevel) + 5;
	}

	public int calcModifierDamage(int level, DamageSource source) {
		return 0;
	}

	public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType) {
		return 0.0F;
	}

	public boolean canApplyTogether(Enchantment ench) {
		return this != ench;
	}

	public Enchantment setName(String enchName) {
		this.name = enchName;
		return this;
	}

	public String getName() {
		return "enchantment." + this.name;
	}

	public String getTranslatedName(int level) {
		String s = StatCollector.translateToLocal(this.getName());
		return s + " " + (Todo.instance.shouldUseRomanianNotation(level) ? StringUtils.romanianNotation(level) : level);
	}

	public boolean canApply(ItemStack stack) {
		return this.type.canEnchantItem(stack.getItem());
	}

	public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {}

	public void onUserHurt(EntityLivingBase user, Entity attacker, int level) {}


}
