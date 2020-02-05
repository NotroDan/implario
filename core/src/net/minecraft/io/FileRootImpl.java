package net.minecraft.io;

import oogle.util.IOUtil;

import java.io.File;

public class FileRootImpl implements FileRoot{
    private final File dir;

    public FileRootImpl(File dir){
        this.dir = dir;
    }

    @Override
    public void write(String name, byte[] array) {
        IOUtil.writeBytes(new File(dir, name), array);
    }

    @Override
    public byte[] read(String name) {
        return IOUtil.readBytes(new File(dir, name));
    }

    @Override
    public void delete(String name) {
        IOUtil.remove(new File(dir, name));
    }
}
