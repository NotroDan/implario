package optifine;

public class CacheLocal {
	private int offsetX = 0;
	private int offsetY = 0;
	private int offsetZ = 0;
	private int[][][] cache;
	private int[] lastZs = null;
	private int lastDz = 0;

	public CacheLocal(int maxX, int maxY, int maxZ) {
		cache = new int[maxX][maxY][maxZ];
		resetCache();
	}

	public void resetCache() {
		for(int[][] twoArray : cache)
			for(int[] oneArray : twoArray)
				for(int i = 0; i < oneArray.length; i++)
					oneArray[i] = -1;
	}

	public void setOffset(int offsetX, int offsetY, int offsetZ) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		resetCache();
	}

	public int get(int x, int y, int z) {
		try {
			lastZs = cache[x - offsetX][y - offsetY];
			lastDz = z - offsetZ;
			return lastZs[lastDz];
		} catch (ArrayIndexOutOfBoundsException arrayindexoutofboundsexception) {
			arrayindexoutofboundsexception.printStackTrace();
			return -1;
		}
	}

	public void setLast(int last) {
		try {
			lastZs[lastDz] = last;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
