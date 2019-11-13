package net.minecraft.resources.load;

import __google_.util.FileIO;
import net.minecraft.logging.Log;
import net.minecraft.util.FileUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DatapackClassLoader extends ClassLoader {

	private final Map<String, byte[]> datapack;
	private final String filename;

	public DatapackClassLoader(File f, ClassLoader parent) throws IOException {
		super(parent);
		byte array[] = new byte[(int)f.length()];
		InputStream in = new FileInputStream(f);
		FileUtil.readInputStream(in, array);
		in.close();
		this.filename = f.getName();
		this.datapack = new HashMap<>();
		ZipInputStream input = new ZipInputStream(new ByteArrayInputStream(array), Charset.forName("ASCII"));
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
		if(name.startsWith("net.minecraft.security"))
			throw new SecurityException("Security not allowed!");

		try {
			String name2 = name.replace('.', '/') + ".class";
			byte entry[] = datapack.get(name2);
			if (entry == null) throw new IOException();
			datapack.remove(name2);
			return defineClass(name.replace('/', '.'), entry, 0, entry.length);
		} catch (IOException ex) {
			try {
				return DatapackClassLoader.getSystemClassLoader().loadClass(name);
			} catch (ClassNotFoundException e) {
				Log.MAIN.error("Tried to load class '" + name + "' from datapack " + filename + " but failed!", ex);
				throw e;
			}
		}
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		byte array[] = datapack.get(name);
		return array == null ? null : new ByteArrayInputStream(array, 0, array.length);
	}

	public void close() {
		datapack.clear();
	}

}
