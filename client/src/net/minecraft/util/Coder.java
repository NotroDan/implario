package net.minecraft.util;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Coder {
    public static String toString(boolean b){
        return b + "";
    }

    public static String toString(double d){
        return d + "";
    }

    public static String toString(float f){
        return f + "";
    }

    public static String toString(long l){
        return l + "";
    }

    public static String toString(int i){
        return i + "";
    }

    public static String toString(short s){
        return s + "";
    }

    public static String toString(byte b){
        return b + "";
    }

    public static String toString(byte array[]){
        return new String(array, Charset.forName("UTF-8"));
    }

    public static String toString(Map<String, String> map){
        if(map == null)return "";
        StringBuilder buffer = new StringBuilder();
        for(Map.Entry<String, String> entry : map.entrySet())
            buffer.append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
        return buffer.toString();
    }

    public static String toString(List<String> list){
        if(list == null)return "";
        StringBuilder buffer = new StringBuilder();
        for(String line : list)
            buffer.append(line).append('\n');
        return buffer.toString();
    }

    public static boolean toBoolean(String line){
        return line != null && line.equals("true");
    }

    public static boolean toBoolean(byte array[]){
        return array.length != 0 && toBoolean(array[0]);
    }

    public static boolean toBoolean(byte bool){
        return bool == 0x1;
    }

    public static double toDouble(String line){
        if(line == null)return 0;
        try{
            return Double.valueOf(line);
        }catch (NumberFormatException ex){
            return 0;
        }
    }

    public static float toFloat(String line){
        if(line == null)return 0;
        try{
            return Float.valueOf(line);
        }catch (NumberFormatException ex){
            return 0;
        }
    }

    public static long toLong(String line){
        if(line == null)return 0;
        try{
            return Long.decode(line);
        }catch (NumberFormatException ex){
            return 0;
        }
    }

    public static long toLong(byte array[]){
        if(array.length != 8)return toInt(array);
        return  (long)array[0] << 56 & 0xff00000000000000L
                | (long)array[1] << 48 & 0xff000000000000L
                | (long)array[2] << 40 & 0xff0000000000L
                | (long)array[3] << 32 & 0xff00000000L
                | (long)array[4] << 24 & 0xff000000L
                | (long)array[5] << 16 & 0xff0000L
                | (long)array[6] << 8 & 0xff00L
                | (long)array[7] & 0xffL;
        //Using '&' for negative numbers
    }

    public static int toInt(String line){
        if(line == null)return 0;
        try{
            return Integer.decode(line);
        }catch (NumberFormatException ex){
            return 0;
        }
    }

    public static int toInt(byte array[]){
        if(array.length != 4)return toShort(array);
        return  array[0] << 24 & 0xff000000 |
                array[1] << 16 & 0xff0000 |
                array[2] << 8 & 0xff00 |
                array[3] & 0xff;
    }

    public static short toShort(String line){
        if(line == null)return 0;
        try{
            return Short.decode(line);
        }catch (NumberFormatException ex){
            return 0;
        }
    }

    public static short toShort(byte array[]){
        if(array.length != 2)return toByte(array);
        return (short)(array[0] << 8 & 0xff00 | array[1] & 0xff);
    }

    public static byte toByte(String line){
        if(line == null)return 0;
        try{
            return Byte.decode(line);
        }catch (NumberFormatException ex){
            return 0;
        }
    }

    public static byte toByte(byte array[]) {
        if(array.length != 1) return 0;
        return (byte) (array[0] & 0xff);
    }

    public static byte toByte(boolean bool){
        return (byte)(bool ? 0x1 : 0x0);
    }

    @SuppressWarnings("unchecked")
    public static <T> T toObject(byte array[], Class<T> clazz){
        if(isPrimitive(clazz))return (T)toPrimitive(clazz, array);
        if(isEnum(clazz))return (T)toEnum(clazz, array);
        ByteUnzip unzip = new ByteUnzip(array);
        T obj = Reflect.create(Reflect.getConstructor(clazz));
        for(Field field : clazz.getDeclaredFields()){
            if(!Reflect.isEditable(field))continue;
            if(!field.isAccessible())field.setAccessible(true);
            Class klass = field.getType();
            byte write[] = unzip.getBytes();
            Reflect.setToField(field, obj, toObject(write, klass));
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    public static byte[] toBytes(Object invoke){
        if(invoke instanceof List)return toBytes((List<String>)invoke);
        Class clazz = invoke.getClass();
        if(clazz == String.class) return toBytes(invoke.toString());
        if(clazz == Boolean.class) return toBytes((boolean)invoke);
        if(clazz == Long.class) return toBytes((long)invoke);
        if(clazz == Integer.class) return toBytes((int)invoke);
        if(clazz == Short.class) return toBytes((short)invoke);
        if(clazz == Byte.class) return toBytes((byte)invoke);
        if(clazz == byte[].class) return (byte[])invoke;
        if(isEnum(clazz))return Coder.toBytes(Reflect.getEnumName(invoke));
        ByteZip zip = new ByteZip();
        for(Field field : clazz.getDeclaredFields()){
            if(!Reflect.isEditable(field))continue;
            if(!field.isAccessible())field.setAccessible(true);
            Object obj = Reflect.getFromField(field, invoke);
            if(obj == null)obj = "null";
            Object primitive = Reflect.getPrimitive(field, invoke);
            zip.add(Coder.toBytes(primitive == null ? obj : primitive));
        }
        return zip.build();
    }

    public static byte[] toBytes(List<String> list){
        ByteZip zip = new ByteZip();
        zip.add(list.size());
        for(String element : list)
            zip.add(element);
        return zip.build();
    }

    public static byte[] toBytes(String line){
        return line.getBytes(Charset.forName("UTF-8"));
    }

    public static byte[] toBytes(boolean b){
        return new byte[]{toByte(b)};
    }

    public static byte[] toBytes(long l){
        if(l >> 32 == l)return toBytes((int)l);
        return new byte[]{
                (byte)(l >> 56),
                (byte)(l >> 48),
                (byte)(l >> 40),
                (byte)(l >> 32),
                (byte)(l >> 24),
                (byte)(l >> 16),
                (byte)(l >> 8),
                (byte)l
        };
    }

    public static byte[] toAbsoluteBytes(int i){
        return new byte[]{
                (byte)(i >> 24),
                (byte)(i >> 16),
                (byte)(i >> 8),
                (byte)i
        };
    }

    public static byte[] toBytes(int i){
        if(i >> 16 == i)return toBytes((short)i);
        return toAbsoluteBytes(i);
    }

    public static byte[] toBytes(short s){
        if(s >> 8 == s)return toBytes((byte) s);
        return new byte[]{
                (byte)(s >> 8),
                (byte)s
        };
    }

    public static byte[] toBytes(byte b){
        return new byte[]{b};
    }

    public static byte[] addBytes(byte array[], byte add[], int start, byte result[]){
        if(array.length != 0)
            System.arraycopy(array, 0, result, start, array.length);
        System.arraycopy(add, 0, result, start + array.length, add.length);
        return result;
    }

    public static byte[] addBytes(byte array[], byte add[], int start){
        return addBytes(array, add, start, new byte[array.length + add.length]);
    }

    public static byte[] addBytes(byte array[], byte add[]){
        return addBytes(array, add, 0);
    }

    public static byte[] addBytes(byte add[], int start, byte result[]){
        System.arraycopy(add, 0, result, start, add.length);
        return result;
    }

    public static byte[] subBytes(byte array[], int size, int start){
        byte result[] = new byte[size];
        System.arraycopy(array, start, result, 0, size);
        return result;
    }

    public static byte[] subBytes(byte array[], int size){
        return subBytes(array, size, 0);
    }

    public static byte[] getSize(int size){
        if(size < 127)return new byte[]{(byte)size};
        return addBytes(new byte[]{(byte)127}, toAbsoluteBytes(size));
    }

    public static int getSize(byte array[], int start){
        byte size = array[start];
        if(size == 127)return toInt(subBytes(array, 4, start + 1));
        return size;
    }

    public static int getSize(byte array[]){
        return getSize(array, 0);
    }

    public static Map<String, String> toMap(String str){
        if(str == null)return new HashMap<>();
        String split[] = str.split("\n");
        Map<String, String> map = new HashMap<>();
        for(String line : split){
            String spl[] = line.split("=");
            if(spl.length == 1) map.put(spl[0], "");
            else map.put(spl[0], spl[1]);
        }
        return map;
    }

    public static List<String> toList(String str){
        if(str == null)return new ArrayList<>();
        String split[] = str.split("\n");
        List<String> list = new ArrayList<>(split.length);
        for(String line : split)
            list.add(line);
        return list;
    }

    public static String toHex(String line){
        return toHex(line.getBytes());
    }

    public static String toHex(byte array[]){
        StringBuffer buffer = new StringBuffer();
        for (byte b : array) {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1) buffer.append('0');
            buffer.append(hex);
        }
        return buffer.toString();
    }

    public static String toLowerEN(String line){
        char result[] = new char[line.length()];
        char chars[] = line.toCharArray();
        for(int i = 0; i < chars.length; i++){
            char c = chars[i];
            if(c > 64 && c < 91) c = (char)(c + 32);
            result[i] = c;
        }
        return new String(result);
    }

    public static String toUpperEN(String line){
        char result[] = new char[line.length()];
        char chars[] = line.toCharArray();
        for(int i = 0; i < chars.length; i++){
            char c = chars[i];
            if(c > 96 && c < 123) c = (char)(c - 32);
            result[i] = c;
        }
        return new String(result);
    }

    public static String toLowerRU(String line){
        char result[] = new char[line.length()];
        char chars[] = line.toCharArray();
        for(int i = 0; i < chars.length; i++){
            char c = chars[i];
            if(c > 1071 && c < 1104) c = (char)(c + 32);
            result[i] = c;
        }
        return new String(result);
    }

    public static String toUpperRU(String line) {
        char result[] = new char[line.length()];
        char chars[] = line.toCharArray();
        for (int i = 0; i < chars.length; i++){
            char c = chars[i];
            if(c > 1039 && c < 1072) c = (char) (c - 32);
            result[i] = c;
        }
        return new String(result);
    }

    public static boolean isEnum(Class clazz){
        return clazz.isEnum();
    }

    public static Object toEnum(Class clazz, byte array[]){
        String name = toString(array);
        for(Object object : clazz.getEnumConstants())
            if(Reflect.getEnumName(object).equals(name))return object;
        return null;
    }

    public static boolean isPrimitive(Class clazz){
        return clazz.isPrimitive() || clazz == String.class || clazz == byte[].class;
    }

    public static Object toPrimitive(Class clazz, byte array[]) {
        if(clazz == String.class) {
            String result = Coder.toString(array);
            return result.equals("null") ? null : result;
        }
        if(clazz == boolean.class) return Coder.toBoolean(array);
        if(clazz == long.class) return Coder.toLong(array);
        if(clazz == int.class) return Coder.toInt(array);
        if(clazz == short.class) return Coder.toShort(array);
        if(clazz == byte.class) return Coder.toByte(array);
        if(clazz == byte[].class) return array;
        return null;
    }
}
