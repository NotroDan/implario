import net.minecraft.client.gui.font.TrueTypeBaker;
import net.minecraft.client.resources.TrueTypeFont;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;

public class TrueTypeBakerTest {

	@Test
	public void test() {

		String fontName = "Segoe UI";
		Assert.assertTrue(TrueTypeFont.isSupported(fontName));
		Font font = new Font(fontName, Font.BOLD + Font.ITALIC, 16);
//		trueTypeFont = new TrueTypeFont(font, true);
		TrueTypeBaker baker = new TrueTypeBaker(font, true);
		baker.bake();
	}

}
