import net.minecraft.client.gui.font.TrueTypeBaker;
import org.junit.Test;

import java.awt.*;

public class TrueTypeBakerTest {

	@Test
	public void test() {
		TrueTypeBaker baker = new TrueTypeBaker(new Font("Verdana", 3, 96), true);
		baker.bake();
	}

}
