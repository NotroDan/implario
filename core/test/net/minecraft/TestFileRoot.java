package net.minecraft;

import net.minecraft.io.FileRoot;
import net.minecraft.io.FileRootImpl;
import oogle.util.IOUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class TestFileRoot {
    @Test
    public void testFileRootImpl(){
        File dir = new File("testing");
        String write = "kek/config.yml";
        File writeFile = new File(dir.getAbsolutePath() + "/" + write);
        FileRoot root = new FileRootImpl(dir);
        root.write(write, new byte[]{17});
        byte array[] = IOUtil.readBytes(writeFile);
        Assert.assertNotNull(array);
        Assert.assertEquals(1, array.length);
        Assert.assertEquals(17, array[0]);
        array = root.read(write);
        Assert.assertNotNull(array);
        Assert.assertEquals(1, array.length);
        Assert.assertEquals(17, array[0]);
        root.delete(write);
        Assert.assertFalse(writeFile.exists());
        dir.delete();
    }
}
