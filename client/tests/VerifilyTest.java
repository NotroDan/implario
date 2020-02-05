import net.minecraft.security.update.SecurityKeys;
import net.minecraft.util.MathHelper;
import net.minecraft.util.crypt.AES;
import net.minecraft.util.crypt.ECDSA;
import net.minecraft.util.crypt.SecurityKey;
import net.minecraft.util.crypt.TimedSertificate;
import oogle.util.IOUtil;
import org.lwjgl.Sys;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.ReflectPermission;
import java.security.Permission;
import java.util.Scanner;

public class VerifilyTest {
    private static int rounds = 99999;
    private static String pswd = "kek";

    public static class TestPrivate{
        public static void main(String[] args) {
            ECDSA ecdsa = SecurityKeys.getTimed(pswd, rounds);
            byte lol[] = "kek".getBytes();
            System.out.println(SecurityKeys.root.getTimed().getSert().verify(lol, ecdsa.signature(lol)));
        }
    }

    public static class TestGeneratePrivate{
        public static void main(String[] args) throws Exception{
            ECDSA pvlt = ECDSA.decodePrivate(IOUtil.readBytes("privateRoot.key"));
            AES aes = AES.generateAES(pswd, rounds);
            ECDSA serti = new ECDSA(384);
            TimedSertificate sertificate = new TimedSertificate(System.currentTimeMillis() + 31557600000L * 2, serti, pvlt);
            SecurityKey key = new SecurityKey(pvlt, sertificate);
            IOUtil.writeBytes(new File("core/src/net/minecraft/security/update/privateTimedKey.aes"), aes.encrypt(serti.encodePrivate()));
            IOUtil.writeBytes(new File("core/src/net/minecraft/security/update/public.keys"), key.encodePublic());
        }
    }

    public static int getNumber(Scanner scanner){
        while (true){
            if(scanner.hasNextInt()){
                int i = scanner.nextInt();
                if(i < 0){
                    System.out.println("Отрицательное число");
                    continue;
                }
                return i;
            }
            scanner.next();
            System.out.println("Не число");
        }
    }
}
