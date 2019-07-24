package net.minecraft.resources.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
			in.read(array);
			in.close();
			files.put(entry.getName(), array);
		}
		zip.close();
	}

	public void writeToJar(File file) throws IOException {
		ZipOutputStream jar = new ZipOutputStream(new FileOutputStream(file), Charset.forName("UTF-8"));
		ZipEntry entry;
		for (Map.Entry<String, byte[]> iter : files.entrySet()) {
			entry = new ZipEntry(iter.getKey());
			jar.putNextEntry(entry);
			if (iter.getValue() != null) jar.write(iter.getValue());
			jar.closeEntry();
		}
		jar.close();
	}

	public void add(String name, byte array[]) {
		files.put(name, array);
	}

	public void remove(String name) {
		files.remove(name);
	}

}
