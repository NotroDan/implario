package net.minecraft.io;

public interface FileRoot {
    void write(String name, byte array[]);

    void delete(String name);

    byte[] read(String name);
}
