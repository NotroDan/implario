package vanilla.client.game.particle;

import net.minecraft.client.MC;
import net.minecraft.server.Todo;
import net.minecraft.util.ParticleType;

public class VanillaParticles {

	public static ParticleType MOB_APPEARANCE;

	public static void register() {
		// ToDo: unregister
		if (Todo.instance.isServerSide()) return;
		MOB_APPEARANCE = new ParticleType("mobappearance", 41, true);
		MC.i().effectRenderer.registerParticle(MOB_APPEARANCE.getParticleID(), new MobAppearance.Factory());
	}

}
