package vanilla.world.gen.layer;

public class GenLayerFuzzyZoom extends GenLayerZoom {

	public GenLayerFuzzyZoom(long p_i2123_1_, GenLayer p_i2123_3_) {
		super(p_i2123_1_, p_i2123_3_);
	}

	/**
	 * returns the most frequently occurring number of the set, or a random number from those provided
	 */
	protected int selectModeOrRandom(int a, int b, int c, int d) {
		return this.selectRandom(new int[] {a, b, c, d});
	}

}
