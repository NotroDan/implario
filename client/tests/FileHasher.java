import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class FileHasher {

	@Test
	public void test() throws IOException {
		File f = new File("server-resource-packs/legacy");
		String s = Hashing.sha1().hashBytes(Files.toByteArray(f)).toString();
		System.out.println(s);
	}

}
