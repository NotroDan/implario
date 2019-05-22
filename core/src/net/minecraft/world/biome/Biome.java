package net.minecraft.world.biome;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockFlower;
import net.minecraft.logging.Log;
import net.minecraft.resources.Domain;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ColorizerFoliage;

import java.util.Random;
import java.util.Set;

public abstract class Biome {

	public static final Biome[] biomeList = new Biome[256];
	public static final BiomeVoid VOID = new BiomeVoid();
	public static final Set<Biome> explorationBiomesList = Sets.newHashSet();

	protected final Domain domain;
	protected final int legacyId;
	protected final String name;
	protected final String address;

	protected Biome(int legacyId, String name, Domain domain) {
		this.legacyId = legacyId;
		this.name = name;
		this.domain = domain;
		this.address = "biome." + name.toLowerCase().replace(' ', '_');
		biomeList[legacyId] = this;
	}

	/**
	 * return the biome specified by biomeID, or 0 (ocean) if out of bounds
	 */
	public static Biome getBiome(int id) {
		return getBiomeFromBiomeList(id, null);
	}

	public static Biome getBiomeFromBiomeList(int biomeId, Biome biome) {
		if (biomeId >= 0 && biomeId <= biomeList.length) {
			Biome biomegenbase = biomeList[biomeId];
			return biomegenbase == null ? biome : biomegenbase;
		}
		Log.MAIN.warn("Biome ID is out of bounds: " + biomeId + ", defaulting to 0 (Ocean)");
		return Biome.VOID;
	}


	public String getName() {
		return name;
	}
	public int getLegacyId() {
		return legacyId;
	}
	public Domain getDomain() {
		return domain;
	}
	public float getFloatTemperature(BlockPos pos) {return 0.5F;}

	public String getAddress() {
		return address;
	}
	/**
	 * takes temperature, returns color
	 */
	public int getSkyColorByTemp(float temp) {
		temp /= 3.0F;
		temp = MathHelper.clamp_float(temp, -1.0F, 1.0F);
		return MathHelper.func_181758_c(0.62222224F - temp * 0.05F, 0.5F + temp * 0.1F, 1.0F);
	}

	public boolean getEnableSnow() {
		return false;
	}

	public boolean canSpawnLightningBolt() {
		return false;
	}

	public boolean isHighHumidity() {
		return false;
	}

	public int getGrassColorAtPos(BlockPos pos) {
		return 0xff44ee44;
	}

	public BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos blockpos1) {
		return rand.nextInt(3) > 0 ? BlockFlower.EnumFlowerType.DANDELION : BlockFlower.EnumFlowerType.POPPY;
	}

	public TempCategory getTempCategory() {
		return TempCategory.MEDIUM;
	}

	public int getIntRainfall() {
		return 0;
	}

	public int getFoliageColorAtPos(BlockPos pos) {
		double d0 = (double) MathHelper.clamp_float(this.getFloatTemperature(pos), 0.0F, 1.0F);
		double d1 = (double) MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
		return ColorizerFoliage.getFoliageColor(d0, d1);
	}

	public float getFloatRainfall() {
		return 0;
	}

	public int getWaterColorMultiplier() {
		return 16777215;
	}

	public float getSpawningChance() {
		return 0.1F;
	}

}
