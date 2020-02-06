package net.minecraft.client.audio;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum SoundCategory {
	MASTER("master", 0, new ItemStack(Items.firework_charge), "§aОбщая громкость", "Изменение этой величины повлияет", "на все остальные звуки."),
	MUSIC("music", 1, new ItemStack(Items.record_11), "§eФоновая музыка", "Изменение громкости милых композиций", "сопровождающих игровой процесс."),
	RECORDS("record", 2, new ItemStack(Blocks.jukebox), "§eГромкость музыкальных блоков", "Изменение громкости пластинок в проигрывателях", "и нотных блоков."),
	WEATHER("weather", 3, new ItemStack(Items.water_bucket), "§eПогода", "Интенсивность шума дождя."),
	BLOCKS("block", 4, new ItemStack(Blocks.stained_hardened_clay, 1, 4), "§eБлоки", "Звуки разрушения и установки блоков,", "шагов, сундуков, и т. п."),
	MOBS("hostile", 5, new ItemStack(Items.skull, 1, 2), "§eМобы", "Крики враждебных мобов,", "например стоны зомби или шипение крипера."),
	ANIMALS("neutral", 6, new ItemStack(Items.egg), "§eЖивотные", "Милые звуки дружелюбных коровок,", "овечек, свинок, курочек, кроликов...", "", "§oНу и стоны жителей ._."),
	PLAYERS("player", 7, new ItemStack(Items.skull, 1, 3), "§eИгроки", "Звуки ударов, жевания, ходьбы", "и прочего вандализма от игроков."),
	AMBIENT("ambient", 8, new ItemStack(Blocks.tallgrass, 1, 2), "§eПрирода", "Звуки лавы, воды, огня,", "и даже странные звуки в пещерах.");

	private static final Map<String, SoundCategory> NAME_CATEGORY_MAP = new HashMap<>();
	private static final Map<Integer, SoundCategory> ID_CATEGORY_MAP = new HashMap<>();
	private final String categoryName;
	private final int categoryId;
	private final ItemStack item;
	private final List<String> description;

	SoundCategory(String name, int id, ItemStack item, String... description) {
		this.item = item;
		this.categoryName = name;
		this.categoryId = id;
		this.description = Arrays.asList(description);
	}

	public String getCategoryName() {
		return this.categoryName;
	}

	public int getCategoryId() {
		return this.categoryId;
	}

	public static SoundCategory getCategory(String name) {
		return NAME_CATEGORY_MAP.get(name);
	}

	static {
		for (SoundCategory soundcategory : values()) {
			if (NAME_CATEGORY_MAP.containsKey(soundcategory.getCategoryName()) || ID_CATEGORY_MAP.containsKey(soundcategory.getCategoryId())) {
				throw new Error("Clash in Sound Category ID & Name pools! Cannot insert " + soundcategory);
			}

			NAME_CATEGORY_MAP.put(soundcategory.getCategoryName(), soundcategory);
			ID_CATEGORY_MAP.put(soundcategory.getCategoryId(), soundcategory);
		}
	}
	public ItemStack getItem() {
		return item;
	}

	public List<String> getDescription() {
		return description;
	}
}
