package net.minecraft.client.resources;

import net.minecraft.client.MC;
import net.minecraft.client.game.particle.EffectRenderer;
import net.minecraft.client.game.particle.IParticleFactory;
import net.minecraft.client.gui.ingame.Module;
import net.minecraft.client.gui.ingame.Modules;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.resources.Provider;
import net.minecraft.resources.Registrar;

import java.util.function.Function;

public class ClientRegistrar {

	private final Registrar delegate;

	public ClientRegistrar(Registrar delegate) {
		this.delegate = delegate;
	}

	public <T extends Entity> void registerEntity(Class<T> type, Render<T> render) {
		Render overridden = render.getRenderManager().getRenderRaw(type);
		delegate.registerMapping(new MappingRender(render.getRenderManager(), type, overridden, render));
	}


	public void registerParticle(int particleID, IParticleFactory factory) {
		EffectRenderer effectRenderer = MC.i().effectRenderer;
		IParticleFactory existing = effectRenderer.getFactory(particleID);
		delegate.registerMapping(new MappingParticle(effectRenderer, particleID, existing, factory));
	}

	public void registerIngameModule(String id, Module module) {
		Module existing = Modules.getModule(id);
		delegate.registerMapping(new MappingIngameModule(id, existing, module));
	}

	public <I, O> void replaceProvider(Provider<I, O> provider, Function<I, O> function) {
		delegate.replaceProvider(provider, function);
	}

}