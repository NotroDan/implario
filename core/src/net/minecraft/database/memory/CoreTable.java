package net.minecraft.database.memory;

import __google_.util.FileIO;
import lombok.RequiredArgsConstructor;
import net.minecraft.database.Table;

import java.io.File;

@RequiredArgsConstructor
public class CoreTable implements Table {
    private final File dir;

    @Override
    public void write(String key, byte[] write) {
        FileIO.writeBytes(new File(dir, key), write);
    }

    @Override
    public void delete(String key) {
        new File(dir, key).delete();
    }

    @Override
    public byte[] read(String key) {
        return FileIO.readBytes(new File(dir, key));
    }
}
