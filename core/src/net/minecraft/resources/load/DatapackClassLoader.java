package net.minecraft.resources.load;

import net.minecraft.logging.Log;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DatapackClassLoader extends ClassLoader {

	private final Map<String, byte[]> datapack;
	private final String filename;

	public DatapackClassLoader(File f, ClassLoader parent) throws IOException {
		super(parent);
		this.filename = f.getName();
		ZipInputStream input = new ZipInputStream(new FileInputStream(f), Charset.forName("ASCII"));
		this.datapack = new HashMap<>();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ZipEntry file;
		while (true) {
			file = input.getNextEntry();
			if (file == null) break;
			while (input.available() > 0)
				out.write(input.read());
			input.closeEntry();
			datapack.put(file.getName(), out.toByteArray());
			out.reset();
		}
		out.close();
		input.close();
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		try {
			String name2 = name.replace('.', '/') + ".class";
			byte entry[] = datapack.get(name2);
			if (entry == null) throw new IOException();
			datapack.remove(name2);
			return defineClass(name.replace('/', '.'), entry, 0, entry.length - 1);
		} catch (IOException ex) {
			try {
				return DatapackClassLoader.getSystemClassLoader().loadClass(name);
			} catch (ClassNotFoundException e) {
				Log.MAIN.error("Tried to load class '" + name + "' from datapack " + filename + " but failed!");
				Log.MAIN.exception(ex);
				throw new RuntimeException(ex);
			}
		}
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		byte array[] = datapack.get(name);
		return array == null ? null : new ByteArrayInputStream(array, 0, array.length - 1);
	}

	public void close() {
		datapack.clear();
	}

}
