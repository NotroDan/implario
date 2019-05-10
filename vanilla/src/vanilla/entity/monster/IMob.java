package vanilla.entity.monster;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import vanilla.entity.passive.IAnimals;

public interface IMob extends IAnimals
{
    Predicate<Entity> mobSelector = new Predicate<Entity>()
    {
        public boolean apply(Entity p_apply_1_)
        {
            return p_apply_1_ instanceof IMob;
        }
    };
    Predicate<Entity> VISIBLE_MOB_SELECTOR = new Predicate<Entity>()
    {
        public boolean apply(Entity p_apply_1_)
        {
            return p_apply_1_ instanceof IMob && !p_apply_1_.isInvisible();
        }
    };
}
