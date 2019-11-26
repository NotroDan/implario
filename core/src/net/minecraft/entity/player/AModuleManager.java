package net.minecraft.entity.player;

public abstract class AModuleManager implements ModuleManager{
    private int id;

    @Override
    public void writeID(int id) {
        this.id = id;
    }

    @Override
    public int readID() {
        return id;
    }
}
