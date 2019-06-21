package net.minecraft.resources.update;

import net.minecraft.resources.update.FileDatapackEdit;

import java.io.File;

public class Main {
    public static void main(String args[]) throws Exception{
        //FileDatapackEdit edit = new FileDatapackEdit(new File("../../lib/Util.jar"));
        //edit.writeToJar(new File("Util.jar"));
        FileDatapackEdit edit = new FileDatapackEdit();
        ManifestCreator creator = new ManifestCreator(new File(".cache"), new File("../../lib/Util.jar"));
        creator.createManifest(new File(".cache")).writeTo(edit);
        edit.writeToJar(new File("Jar.jar"));
    }
}
