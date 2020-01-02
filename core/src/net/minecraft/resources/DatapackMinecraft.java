package net.minecraft.resources;

public class DatapackMinecraft extends Datapack{
    public static final String MINECRAFT = "minecraft";

    public DatapackMinecraft() {
        super(MINECRAFT);
    }

    @Override
    public void preinit() {
        registrar.registerCommands(

        );
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void disable() {
        super.disable();
    }

    @Override
    protected void unload() {
        super.unload();
    }
}
