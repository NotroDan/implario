import net.minecraft.security.update.JarFile;
import net.minecraft.security.update.SecurityKeys;
import net.minecraft.security.update.SignedUpdate;
import net.minecraft.security.update.Update;
import net.minecraft.util.byteable.Encoder;
import net.minecraft.util.byteable.FastDecoder;
import net.minecraft.util.byteable.FastEncoder;
import net.minecraft.util.crypt.ECDSA;

import java.io.File;

public class UpdateTest {
    public static void main(String[] args) throws Exception{
        File testJar = new File("./out\\artifacts\\release\\gamedata\\client.jar");
        JarFile oneJar = new JarFile(testJar);
        Encoder encoder = new FastEncoder();
        Update.generate(oneJar, encoder, false);
        ECDSA cert = new ECDSA(384);
        JarFile path = new JarFile(testJar);
        oneJar.remove("lolkek");
        oneJar.add("lol", new byte[]{});
        Update update = Update.generate(oneJar, new FastDecoder(encoder.generate()), false);
        SignedUpdate signed = update.toSignedUpdate();
        signed.verify(cert);
        Encoder enc = new FastEncoder();
        signed.encode(enc);
        signed = new SignedUpdate(new FastDecoder(enc.generate()));
        System.out.println(signed.check(SecurityKeys.root.getTimed().getSert()));
        update = signed.getUpdate();
        update.writeTo(path);
        path.writeToJar(testJar);
    }
}
