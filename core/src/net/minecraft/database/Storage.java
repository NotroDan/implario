package net.minecraft.database;

public interface Storage {
    Table get(String name);

    Table create(String name);

    default Table getCoreTable(){
        return create("core");
    }

    void remove(String name);

    void close();
}
