package net.minecraft.client.resources;

import net.minecraft.client.MC;
import net.minecraft.client.game.particle.EffectRenderer;
import net.minecraft.client.game.particle.IParticleFactory;
import net.minecraft.client.gui.ingame.Module;
import net.minecraft.client.gui.ingame.Modules;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.resources.Provider;
import net.minecraft.resources.Registrar;

import java.util.function.Function;

public class ClientRegistrar {

	private final Registrar delegate;

	public ClientRegistrar(Registrar delegate) {
		this.delegate = delegate;
	}

	public <T extends Entity> void registerEntity(Class<T> type, Render<T> render) {
		Render<T> overridden = render.getRenderManager().getRenderRaw(type);
		delegate.registerMapping(new MappingRender<T>(render.getRenderManager(), type, overridden, render));
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

	public void registerItem(Item item, int meta, ModelResourceLocation location) {
		RenderItem renderItem = MC.getRenderItem();
		ModelResourceLocation existing = renderItem.getItemModelMesher().getLocation(item, meta);
		delegate.registerMapping(new MappingRenderItem(renderItem.getItemModelMesher(), item, meta, location, existing));
	}

}