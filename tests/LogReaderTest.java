import net.minecraft.logging.LogLevel;
import net.minecraft.logging.LogReader;
import org.junit.Assert;
import org.junit.Test;

public class LogReaderTest {

	@Test
	public void testLogReading() {

		LogReader.Line s1 = LogReader.constructLine("08:14:62 ! Something is wrong...");
		LogReader.Line s2 = LogReader.constructLine("08:14:62 > ");
		LogReader.Line s3 = LogReader.constructLine("08:14:62 >");
		Assert.assertEquals(s1, new LogReader.Line(LogLevel.WARNING, "Something is wrong...", "08:14:62"));
		Assert.assertEquals(s2, new LogReader.Line(LogLevel.INFO, "", "08:14:62"));
		Assert.assertEquals(s3, new LogReader.Line(LogLevel.COMMENT, "08:14:62 >", null));

	}


}
