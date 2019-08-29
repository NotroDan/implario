import net.minecraft.database.Storage;
import net.minecraft.database.Table;
import net.minecraft.database.mariadb.MariaDBStorage;
import net.minecraft.database.memory.MemoryStorage;

public class StorageTest {
    public static class MariaDB{
        public static void main(String[] args) {
            //Тестовые данные от локальной базы данных
            Storage storage = new MariaDBStorage("192.168.43.200", 3306, "minecraft", "123456", "minecraft");
            storageTest(storage);
        }
    }

    public static class Memory{
        public static void main(String[] args) {
            storageTest(new MemoryStorage(null, false));
        }
    }

    private static void storageTest(Storage storage){
        storage.remove("lol");
        Table table = storage.create("core2");
        table.write("lol", "lol".getBytes());
        table.write("lol", "kek".getBytes());
        long l = System.nanoTime();
        byte array[] = table.read("lol");
        byte array2[] = table.read("lol2");
        long e = System.nanoTime();
        System.out.println(new String(array));
        System.out.println((e - l));
    }
}
