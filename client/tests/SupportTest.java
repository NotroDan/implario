import __google_.util.FileIO;
import net.minecraft.security.update.SecurityKeys;
import net.minecraft.util.crypt.AES;
import net.minecraft.util.crypt.ECDSA;
import net.minecraft.util.crypt.TimedSertificate;

import java.io.File;
import java.security.SecureRandom;

public class SupportTest {
    private static int rounds = 99999;
    private static String pswd = "kek";

    public static class TestPrivate{
        public static void main(String[] args) {
            ECDSA ecdsa = SecurityKeys.getTimed(pswd, rounds);
            byte lol[] = "kek".getBytes();
            System.out.println(SecurityKeys.sertificate.getSert().verify(lol, ecdsa.signature(lol)));
        }
    }

    public static class TestGeneratePrivate{
        public static void main(String[] args) throws Exception{
            ECDSA pvlt = ECDSA.decodePrivate(FileIO.readBytes("privateRoot.key"));
            AES aes = AES.generateAES(pswd, rounds);
            ECDSA serti = new ECDSA(384);
            TimedSertificate sertificate = new TimedSertificate(System.currentTimeMillis() + 31557600000L * 2, serti, pvlt);
            FileIO.writeBytes(new File("core/src/net/minecraft/security/update/privateTimedKey.aes"), aes.encrypt(serti.encodePrivate()));
            FileIO.writeBytes(new File("core/src/net/minecraft/security/update/public.sertificate"), sertificate.encode());
        }
    }
}
