package vanilla.client.game.particle;

import net.minecraft.client.resources.ClientRegistrar;
import net.minecraft.client.resources.ClientSideLoadable;
import net.minecraft.util.ParticleType;

public class VanillaParticles implements ClientSideLoadable {

	public static ParticleType MOB_APPEARANCE;

	@Override
	public void load(ClientRegistrar registrar) {
		MOB_APPEARANCE = new ParticleType("mobappearance", 41, true);
		registrar.registerParticle(MOB_APPEARANCE.getParticleID(), new MobAppearance.Factory());
	}

}
