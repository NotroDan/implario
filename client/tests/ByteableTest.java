import net.minecraft.util.byteable.*;

public class ByteableTest {
    public static void main(String args[]) {
        SlowEncoder encoder = new SlowEncoder();
        encoder.setUseCompressOfShort(true);
        encoder.setUsingCompressACSII(true);
        encoder.setSizeCompressOfInt(3);
        encoder.setSizeCompressOfLong(6);

        encoder.writeBytes(new byte[]{124});
        encoder.writeBoolean(true);
        encoder.writeByte((byte)3);
        encoder.writeShort((short)125);
        encoder.writeInt(-1);
        encoder.writeLong(2571825791250L);
        encoder.writeFloat(0.4214124F);
        encoder.writeDouble(0.125125125125D);
        encoder.writeDouble(0.125125125125D);
        encoder.writeDouble(0.125125125125D);
        encoder.writeDouble(0.125125125125D);
        encoder.writeDouble(0.125125125125D);
        encoder.writeDouble(0.125125125125D);
        encoder.writeString("String");
        //
        byte array[] = new byte[24124124];
        encoder.generate(array, 6666);
        SlowDecoder decoder = new SlowDecoder(array, 6666);
        decoder.setUseCompressOfShort(true);
        decoder.setUsingCompressACSII(true);
        decoder.setSizeCompressOfInt(3);
        decoder.setSizeCompressOfLong(6);

        System.out.println(decoder.readBytes()[0]);
        System.out.println(decoder.readBoolean());
        System.out.println(decoder.readByte());
        System.out.println(decoder.readShort());
        System.out.println(decoder.readInt());
        System.out.println(decoder.readLong());
        System.out.println(decoder.readFloat());
        System.out.println(decoder.readDouble());
        System.out.println(decoder.readDouble());
        System.out.println(decoder.readDouble());
        System.out.println(decoder.readDouble());
        System.out.println(decoder.readDouble());
        System.out.println(decoder.readDouble());
        System.out.println(decoder.readStr());
    }
}
