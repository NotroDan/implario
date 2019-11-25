import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.util.Tree;

public class TreeTest {

	public static void main(String[] args) {
		Tree<L> tree = new Tree<>(new L("root"));
		L anim = new L("walking");
		L bird = new L("flying");

		L dog = new L("dog");
		L korgi = new L("korgi");
		L cat = new L("cat");
		L duck = new L("duck");
		L pigeon = new L("pigeon");
		L plane = new L("plane");

		tree.getRootElement().growBranch(anim);
		tree.getRootElement().growBranch(bird);

		anim.growBranch(dog);
		dog.growBranch(korgi);
		anim.growBranch(cat);
		anim.growBranch(duck);

		bird.growBranch(duck);
		bird.growBranch(pigeon);
		bird.growBranch(plane);

		tree.rebuild();

		for (L l : tree.unloadingOrder()) {
			System.out.print(l + ", ");
		}
	}

	@Data
	@EqualsAndHashCode(callSuper = false)
	static class L extends Tree.Leaf {

		private final String name;

		public String toString() {
			return name;
		}
	}

}
