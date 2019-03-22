package net.minecraft.client.gui.map;

public class MapTile {

	private final int size;
	private final byte[] colors;

	public MapTile(int size) {
		this.size = size;
		this.colors = new byte[size * size * 3];
	}

	public void setPixel(int x, int y, byte red, byte green, byte blue) {
		int i = y * size + x;
		colors[i] = red;
		colors[i + 1] = green;
		colors[i + 2] = blue;
	}

	public void setPixel(int x, int y, int color) {
		byte red = (byte) ((color & 0xff0000) >> 4) ;
		byte green = (byte) ((color & 0xff00) >> 2);
		byte blue = (byte) (color & 0xff);
		setPixel(x, y, red, green, blue);
	}

	public byte[] getColors() {
		return colors;
	}

	public int getSize() {
		return size;
	}

	public void draw() {
//		MC.i().getModelManager().getBlockModelShapes().getModelForState(Blocks.clay.getDefaultState()).getFaceQuads(EnumFacing.UP).get(0).getSprite()
	}

}
