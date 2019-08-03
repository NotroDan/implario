package optifine;

public class CacheLocalByte {
	private int offsetX = 0;
	private int offsetY = 0;
	private int offsetZ = 0;
	private byte[][][] cache;
	private byte[] lastZs = null;
	private int lastDz = 0;

	public CacheLocalByte(int maxX, int maxY, int maxZ) {
		cache = new byte[maxX][maxY][maxZ];
		resetCache();
	}

	public void resetCache() {
		for(byte[][] twoArray : cache)
			for(byte[] oneArray : twoArray)
				for(int i = 0; i < oneArray.length; i++)
					oneArray[i] = -1;
	}

	public void setOffset(int offsetX, int offsetY, int offsetZ) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		resetCache();
	}

	public byte get(int x, int y, int z) {
		try {
			lastZs = cache[x - offsetX][y - offsetY];
			this.lastDz = z - offsetZ;
			return this.lastZs[lastDz];
		} catch (ArrayIndexOutOfBoundsException arrayindexoutofboundsexception) {
			arrayindexoutofboundsexception.printStackTrace();
			return (byte) -1;
		}
	}

	public void setLast(byte last) {
		try {
			lastZs[lastDz] = last;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
