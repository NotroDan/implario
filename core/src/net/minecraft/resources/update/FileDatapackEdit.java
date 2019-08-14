package net.minecraft.resources.update;

import __google_.util.FileIO;
import net.minecraft.util.FileUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileDatapackEdit {

	final Map<String, byte[]> files = new HashMap<>();

	public FileDatapackEdit() {}

	public FileDatapackEdit(File file) throws IOException {
		ZipFile zip = new ZipFile(file);
		ZipEntry entry;
		byte array[];
		InputStream in;
		Enumeration<? extends ZipEntry> iterator = zip.entries();
		while (iterator.hasMoreElements()) {
			entry = iterator.nextElement();
			in = zip.getInputStream(entry);
			array = new byte[(int) entry.getSize()];
			FileUtil.readInputStream(in, array);
			in.close();
			files.put(entry.getName(), array);
		}
		zip.close();
	}

	public void writeToJar(File file) throws IOException {
		FileIO.writeBytes(file, writeToByteArray());
	}

	public byte[] writeToByteArray() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ZipOutputStream jar = new ZipOutputStream(out, Charset.forName("UTF-8"));
		ZipEntry entry;
		for (Map.Entry<String, byte[]> iter : files.entrySet()) {
			entry = new ZipEntry(iter.getKey());
			jar.putNextEntry(entry);
			if (iter.getValue() != null)
				jar.write(iter.getValue());
			jar.closeEntry();
		}
		jar.flush();
		jar.finish();
		jar.close();
		return out.toByteArray();
	}

	public void add(String name, byte array[]) {
		files.put(name, array);
	}

	public void remove(String name) {
		files.remove(name);
	}

}
