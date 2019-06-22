package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Этот класс отвечает за модельки предметов (в инвентарях, в руках, на земле)
 */
@RequiredArgsConstructor
public class ItemModelMesher {

	// Простые модельки по индексу
	private final Map<Integer, ModelResourceLocation> simpleShapes = Maps.newHashMap();

	// Сложные генераторы моделек по предмету
	private final Map<Item, ItemMeshDefinition> shapers = Maps.newHashMap();

	// Запечённые модельки
	private final Map<Integer, IBakedModel> simpleShapesCache = Maps.newHashMap();

	private final ModelManager modelManager;

	public TextureAtlasSprite getParticleIcon(Item item) {
		return this.getParticleIcon(item, 0);
	}

	public TextureAtlasSprite getParticleIcon(Item item, int meta) {
		return this.getItemModel(new ItemStack(item, 1, meta)).getParticleTexture();
	}

	public IBakedModel getItemModel(ItemStack stack) {
		Item item = stack.getItem();
		int meta = stack.isItemStackDamageable() ? 0 : stack.getMetadata();

		IBakedModel model = simpleShapesCache.get(getIndex(item, meta));

		if (model == null) {
			ItemMeshDefinition shaper = shapers.get(item);
			if (shaper != null) model = modelManager.getModel(shaper.getModelLocation(stack));
		}

		if (model == null) model = modelManager.getMissingModel();

		return model;
	}

	private static int getIndex(Item item, int meta) {
		return Item.getIdFromItem(item) << 16 | meta;
	}

	public ModelResourceLocation getLocation(Item item, int meta) {
		return simpleShapes.get(getIndex(item, meta));
	}

	public void registerModelLocation(Item item, int meta, ModelResourceLocation location) {
		this.simpleShapes.put(getIndex(item, meta), location);
		this.simpleShapesCache.put(getIndex(item, meta), this.modelManager.getModel(location));
	}

	public void unregisterModelLocation(Item item, int meta) {
		int index = getIndex(item, meta);
		this.simpleShapes.remove(index);
		this.simpleShapesCache.remove(index);
	}

	public void registerMeshDefinition(Item item, ItemMeshDefinition definition) {
		this.shapers.put(item, definition);
	}

	public ModelManager getModelManager() {
		return this.modelManager;
	}

	public void rebuildCache() {
		this.simpleShapesCache.clear();

		for (Entry<Integer, ModelResourceLocation> entry : this.simpleShapes.entrySet()) this.simpleShapesCache.put(entry.getKey(), this.modelManager.getModel(entry.getValue()));
	}

}
