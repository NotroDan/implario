package net.minecraft.database.memory;

import net.minecraft.database.Storage;
import net.minecraft.database.Table;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MemoryStorage implements Storage {
    private final Map<String, MemoryTable> storage = new HashMap<>();
    private final File dir;
    private final boolean dumpsEveryWrite;

    public MemoryStorage(File dir, boolean dumpsEveryWrite){
        this.dir = dir;
        this.dumpsEveryWrite = dumpsEveryWrite;
        if(dir != null)dir.mkdir();
    }

    @Override
    public Table get(String name) {
        return storage.get(name);
    }

    @Override
    public Table create(String name) {
        MemoryTable table = storage.get(name);
        if(table == null){
            table = new MemoryTable(dumpsEveryWrite, dir == null ? null : new File(dir, name));
            storage.put(name, table);
        }
        return table;
    }

    @Override
    public void remove(String name) {
        MemoryTable table = storage.remove(name);
        if(table != null)table.delete();
    }

    @Override
    public void close() {
        for(MemoryTable table : storage.values())
            table.close();
    }

    @Override
    public Table getCoreTable() {
        return dir == null ? create("core") : new CoreTable(dir);
    }
}
