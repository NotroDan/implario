package net.minecraft.client.renderer.block.model;

import net.minecraft.client.resources.model.ModelBakery;

import java.io.File;
import java.io.IOException;

public class Converter {

	public static void main(String[] args) throws IOException {
		File dir = new File("\\Users\\DelfikPro\\Desktop\\Разное\\Git\\Implario\\client\\resources\\assets\\minecraft\\models\\item");
		for (File file : dir.listFiles()) {
			ModelBakery.rewriteModel(file);
			System.out.println("Converted " + file + " successfully");
		}
	}

}
