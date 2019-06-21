package net.minecraft.client.resources;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.resources.mapping.Mapping;

public class MappingRender<T extends Entity> extends Mapping<Render<T>> {

	private final RenderManager renderManager;
	private final Class<T> entity;

	public MappingRender(RenderManager renderManager, Class<T> entity, Render<T> overridden, Render<T> actual) {
		super(entity.getName(), overridden, actual);
		this.entity = entity;
		this.renderManager = renderManager;
	}

	@Override
	public void map(Render<T> render) {
		if (render == null) renderManager.removeMapping(entity);
		else renderManager.regMapping(entity, render);
	}

}
