package net.minecraft.client.gui.settings;

public class Tab {

	private final String name, description;
	private final TexQuad texture;
	private final TabScreen render;

	public Tab(String name, TexQuad texture, TabScreen render, String description) {
		this.name = name;
		this.description = description;
		this.texture = texture;
		this.render = render;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public TabScreen getRender() {
		return render;
	}

	public TexQuad getTexture() {
		return texture;
	}

}
