package vanilla.entity;

import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import vanilla.world.gen.feature.village.MerchantRecipe;
import vanilla.world.gen.feature.village.MerchantRecipeList;

public interface IMerchant {

	void setCustomer(Player p_70932_1_);

	Player getCustomer();

	MerchantRecipeList getRecipes(Player p_70934_1_);

	void setRecipes(MerchantRecipeList recipeList);

	void useRecipe(MerchantRecipe recipe);

	/**
	 * Notifies the merchant of a possible merchantrecipe being fulfilled or not. Usually, this is just a sound byte
	 * being played depending if the suggested itemstack is not null.
	 */
	void verifySellingItem(ItemStack stack);

	/**
	 * Get the formatted ChatComponent that will be used for the sender's username in chat
	 */
	IChatComponent getDisplayName();

}
