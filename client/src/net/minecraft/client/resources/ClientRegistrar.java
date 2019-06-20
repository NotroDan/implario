package net.minecraft.client.resources;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.resources.Registrar;

public class ClientRegistrar {

	private final Registrar delegate;

	public ClientRegistrar(Registrar delegate) {
		this.delegate = delegate;
	}

	public <T extends Entity> void registerEntity(Class<T> type, Render<T> render) {
		if (!delegate.getEntities().contains(type)) {
			// ToDo: Более абстракная реализация маппингов.
			throw new IllegalArgumentException("EntityType " + type.getName() + " wasn't found in datapacks' entities.");
		}
		render.getRenderManager().regMapping(type, render);
	}

}
