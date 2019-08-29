package net.minecraft.database;

public interface Table {
    void write(String key, byte[] write);

    void delete(String key);

    byte[] read(String key);
}
