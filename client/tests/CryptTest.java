import net.minecraft.util.crypt.AES;
import net.minecraft.util.crypt.ECDSA;
import net.minecraft.util.crypt.RSA;

public class CryptTest {
    public static class RSATest{
        public static void main(String[] args) {
            RSA rsa = new RSA(2048);
            byte array[] = "lkpkpokpokpokpokpokpojopjojjopopjojojopjojojpojpopojoopjjpojopjoppjoojppojopjjoppojjopjopjpjopjojpojpojopjopjopjpjpojopjjopjopjopjopjopjokjopjopkopеквс8888888888888888е6не6еп767аме7ананаеаol".getBytes();
            RSA publc = RSA.decodePublic(rsa.encodePublic());
            RSA pvlt = RSA.decodePrivate(rsa.encodePrivate());
            byte v[] = publc.encode(array);
            System.out.println(new String(pvlt.decode(v)));
        }
    }

    public static class AESTest {
        public static void main(String[] args) {
            AES aes = new AES(AES.getRandomAESKey());
            aes = new AES(aes.key());
            System.out.println(new String(aes.decrypt(aes.encrypt("lol".getBytes()))));
        }
    }

    public static class ECDSATest {
        public static void main(String[] args) {
            ECDSA ecdsa = new ECDSA(256);
            byte array[] = "lkpkpokpokpokpokpokpojopjojjopopjojojopjojojpojpopojoopjjpojopjoppjoojppojopjjoppojjopjopjpjopjojpojpojopjopjopjpjpojopjjopjopjopjopjopjokjopjopkopеквс8888888888888888е6не6еп767аме7ананаеаol".getBytes();
            ECDSA publc = ECDSA.decodePublic(ecdsa.encodePublic());
            ECDSA pvlt = ECDSA.decodePrivate(ecdsa.encodePrivate());
            byte v[] = pvlt.signature(array);
            System.out.println(publc.verify(array, v));
        }
    }
}
