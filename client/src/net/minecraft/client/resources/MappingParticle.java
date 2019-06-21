package net.minecraft.client.resources;

import net.minecraft.client.game.particle.EffectRenderer;
import net.minecraft.client.game.particle.IParticleFactory;
import net.minecraft.resources.mapping.LegacyMapping;

public class MappingParticle extends LegacyMapping<IParticleFactory> {

	private final EffectRenderer renderer;

	public MappingParticle(EffectRenderer renderer, int particleID, IParticleFactory existing, IParticleFactory replacement) {
		// ToDo: У частиц должны быть имена, а не ID.
		super(particleID, replacement.getClass().getName().toLowerCase(), existing, replacement);
		this.renderer = renderer;
	}

	@Override
	public void map(IParticleFactory element) {
		renderer.unregisterParticle(id);
		renderer.registerParticle(id, element);
	}

}
