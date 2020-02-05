package vanilla.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IAnimals;

import java.util.function.Predicate;

public interface IMob extends IAnimals {

	Predicate<Entity> mobSelector = new Predicate<Entity>() {
		public boolean test(Entity p_apply_1_) {
			return p_apply_1_ instanceof IMob;
		}
	};
	Predicate<Entity> VISIBLE_MOB_SELECTOR = new Predicate<Entity>() {
		public boolean test(Entity p_apply_1_) {
			return p_apply_1_ instanceof IMob && !p_apply_1_.isInvisible();
		}
	};

}
