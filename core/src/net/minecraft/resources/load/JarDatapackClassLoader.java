package net.minecraft.resources.load;

import net.minecraft.logging.Log;
import net.minecraft.resources.DatapackManager;
import net.minecraft.util.FileUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class JarDatapackClassLoader extends ClassLoader {
	private final Map<String, Class<?>> classes = new HashMap<>();
	private final Map<String, byte[]> datapack;
	private final String filename;

	public JarDatapackClassLoader(String filename, byte array[], ClassLoader parent) throws IOException {
		super(parent);
		this.filename = filename;
		this.datapack = new HashMap<>();
		ZipInputStream input = new ZipInputStream(new ByteArrayInputStream(array), StandardCharsets.US_ASCII);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ZipEntry file;
		byte b[] = new byte[2048];
		while (true) {
			file = input.getNextEntry();
			if (file == null) break;
			int i;
			while ((i = input.read(b)) != -1)
				out.write(b, 0, i);
			input.closeEntry();
			datapack.put(file.getName(), out.toByteArray());
			out.reset();
		}
		out.close();
		input.close();
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		/*if(name.startsWith("java.lang.reflect") sun.reflect)
			throw new SecurityException("Reflect not allowed!");
		if(name.equals("java.lang.Class"))
			throw new SecurityException("Getting class not allowed!");
		if(name.equals("java.lang.ClassLoader"))
			throw new SecurityException("ClassLoader not allowed!");*/
		if (name.startsWith("net.minecraft.security"))
			throw new SecurityException("Security not allowed!");
		Class<?> clazz = loadLocalClass(name);
		if (clazz != null) return clazz;
		try {
			return JarDatapackClassLoader.getSystemClassLoader().loadClass(name);
		} catch (ClassNotFoundException ex) {
			//Not found in classpath
		}
		for (DatapackLoader loader : DatapackManager.getLoaders()){
			clazz = loader.getLocalClass(name);
			if(clazz != null)return clazz;
		}

		ClassNotFoundException ex = new ClassNotFoundException();
		Log.MAIN.error("Tried to load class '" + name + "' from datapack " + filename + " but failed!", ex);
		throw ex;
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		byte array[] = datapack.get(name);
		return array == null ? null : new ByteArrayInputStream(array, 0, array.length);
	}

	void close() {
		datapack.clear();
		classes.clear();
	}

	Class<?> getLocalClass(String name){
		Class<?> clazz = classes.get(name);
		return clazz == null ? loadLocalClass(name) : clazz;
	}

	private Class<?> loadLocalClass(String name){
		String fileName = name.replace('.', '/') + ".class";
		byte entry[] = datapack.get(fileName);
		if (entry == null) return null;
		datapack.remove(fileName);
		Class<?> clazz = defineClass(name, entry, 0, entry.length);
		classes.put(name, clazz);
		return clazz;
	}
}
