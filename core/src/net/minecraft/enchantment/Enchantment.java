package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.server.Todo;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;

import java.util.*;

public abstract class Enchantment {

	public static final Enchantment[] enchantmentsBookList;
	protected static final Enchantment[] enchantmentsList = new Enchantment[256];
	private static final Map<ResourceLocation, Enchantment> locationEnchantments = new HashMap<>();
	static {
		List<Enchantment> list = new ArrayList<>();

		for (Enchantment enchantment : enchantmentsList)
			if (enchantment != null) list.add(enchantment);

		enchantmentsBookList = list.toArray(new Enchantment[0]);
	}
	public final int effectId;
	private final int weight;
	public EnumEnchantmentType type;
	protected String name;

	protected Enchantment(int enchID, ResourceLocation enchName, int enchWeight, EnumEnchantmentType enchType) {
		this.effectId = enchID;
		this.weight = enchWeight;
		this.type = enchType;

		if (enchantmentsList[enchID] != null)
			throw new IllegalArgumentException("Duplicate enchantment id!");
		enchantmentsList[enchID] = this;
		locationEnchantments.put(enchName, this);
	}

	public static Enchantment getEnchantmentById(int enchID) {
		return enchID >= 0 && enchID < enchantmentsList.length ? enchantmentsList[enchID] : null;
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

	public String getName() {
		return "enchantment." + this.name;
	}

	public Enchantment setName(String enchName) {
		this.name = enchName;
		return this;
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
