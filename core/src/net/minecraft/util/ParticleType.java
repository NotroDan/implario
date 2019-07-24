package net.minecraft.util;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ParticleType {

	public static final ParticleType
			EXPLOSION_NORMAL = new ParticleType("explode", 0, true),
			EXPLOSION_LARGE = new ParticleType("largeexplode", 1, true),
			EXPLOSION_HUGE = new ParticleType("hugeexplosion", 2, true),
			FIREWORKS_SPARK = new ParticleType("fireworksSpark", 3, false),
			WATER_BUBBLE = new ParticleType("bubble", 4, false),
			WATER_SPLASH = new ParticleType("splash", 5, false),
			WATER_WAKE = new ParticleType("wake", 6, false),
			SUSPENDED = new ParticleType("suspended", 7, false),
			SUSPENDED_DEPTH = new ParticleType("depthsuspend", 8, false),
			CRIT = new ParticleType("crit", 9, false),
			CRIT_MAGIC = new ParticleType("magicCrit", 10, false),
			SMOKE_NORMAL = new ParticleType("smoke", 11, false),
			SMOKE_LARGE = new ParticleType("largesmoke", 12, false),
			SPELL = new ParticleType("spell", 13, false),
			SPELL_INSTANT = new ParticleType("instantSpell", 14, false),
			SPELL_MOB = new ParticleType("mobSpell", 15, false),
			SPELL_MOB_AMBIENT = new ParticleType("mobSpellAmbient", 16, false),
			SPELL_WITCH = new ParticleType("witchMagic", 17, false),
			DRIP_WATER = new ParticleType("dripWater", 18, false),
			DRIP_LAVA = new ParticleType("dripLava", 19, false),
			VILLAGER_ANGRY = new ParticleType("angryVillager", 20, false),
			VILLAGER_HAPPY = new ParticleType("happyVillager", 21, false),
			TOWN_AURA = new ParticleType("townaura", 22, false),
			NOTE = new ParticleType("note", 23, false),
			PORTAL = new ParticleType("portal", 24, false),
			ENCHANTMENT_TABLE = new ParticleType("enchantmenttable", 25, false),
			FLAME = new ParticleType("flame", 26, false),
			LAVA = new ParticleType("lava", 27, false),
			FOOTSTEP = new ParticleType("footstep", 28, false),
			CLOUD = new ParticleType("cloud", 29, false),
			REDSTONE = new ParticleType("reddust", 30, false),
			SNOWBALL = new ParticleType("snowballpoof", 31, false),
			SNOW_SHOVEL = new ParticleType("snowshovel", 32, false),
			SLIME = new ParticleType("slime", 33, false),
			HEART = new ParticleType("heart", 34, false),
			BARRIER = new ParticleType("barrier", 35, false),
			ITEM_CRACK = new ParticleType("iconcrack_", 36, false, 2),
			BLOCK_CRACK = new ParticleType("blockcrack_", 37, false, 1),
			BLOCK_DUST = new ParticleType("blockdust_", 38, false, 1),
			WATER_DROP = new ParticleType("droplet", 39, false),
			ITEM_TAKE = new ParticleType("take", 40, false);
	private static final Map<Integer, ParticleType> PARTICLES = Maps.newHashMap();
	private static final List<String> PARTICLE_NAMES = new ArrayList<>();
	private final String particleName;
	private final int particleID;
	private final boolean shouldIgnoreRange;
	private final int argumentCount;

	private ParticleType(String particleNameIn, int particleIDIn, boolean ignoreRange, int argumentCountIn) {
		this.particleName = particleNameIn;
		this.particleID = particleIDIn;
		this.shouldIgnoreRange = ignoreRange;
		this.argumentCount = argumentCountIn;
		PARTICLES.put(particleID, this);
		if (!particleName.endsWith("_")) PARTICLE_NAMES.add(particleName);
	}

	public ParticleType(String particleNameIn, int particleIDIn, boolean ignoreRange) {
		this(particleNameIn, particleIDIn, ignoreRange, 0);
	}

	public static List<String> getParticleNames() {
		return PARTICLE_NAMES;
	}

	public static Collection<ParticleType> getAll() {
		return PARTICLES.values();
	}

	/**
	 * Gets the relative EnumParticleTypes by id.
	 */
	public static ParticleType getParticleFromId(int particleId) {
		return PARTICLES.get(particleId);
	}

	public String getParticleName() {
		return this.particleName;
	}

	public int getParticleID() {
		return this.particleID;
	}

	public int getArgumentCount() {
		return this.argumentCount;
	}

	public boolean getShouldIgnoreRange() {
		return this.shouldIgnoreRange;
	}

	public boolean hasArguments() {
		return this.argumentCount > 0;
	}

}
