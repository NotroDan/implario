package net.minecraft.client.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.settings.Settings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.TrueFileFilter;
import net.minecraft.client.LogManager;
import net.minecraft.client.Logger;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class ResourcePackRepository {

	private static final Logger logger = Logger.getInstance();
	private static final FileFilter resourcePackFilter = f -> {
		boolean flag = f.isFile() && f.getName().endsWith(".zip");
		boolean flag1 = f.isDirectory() && new File(f, "pack.mcmeta").isFile();
		return flag || flag1;
	};
	private final File dirResourcepacks;
	public final IResourcePack rprDefaultResourcePack;
	private final File dirServerResourcepacks;
	public final IMetadataSerializer rprMetadataSerializer;
	private IResourcePack resourcePackInstance;
	private final ReentrantLock lock = new ReentrantLock();
	private ListenableFuture<Object> field_177322_i;
	private List<ResourcePackRepository.Entry> repositoryEntriesAll = Lists.newArrayList();
	private List<ResourcePackRepository.Entry> repositoryEntries = Lists.newArrayList();

	public ResourcePackRepository(File dirResourcepacksIn, File dirServerResourcepacksIn, IResourcePack rprDefaultResourcePackIn, IMetadataSerializer rprMetadataSerializerIn) {
		this.dirResourcepacks = dirResourcepacksIn;
		this.dirServerResourcepacks = dirServerResourcepacksIn;
		this.rprDefaultResourcePack = rprDefaultResourcePackIn;
		this.rprMetadataSerializer = rprMetadataSerializerIn;
		this.fixDirResourcepacks();
		this.updateRepositoryEntriesAll();
		Iterator<String> iterator = Settings.resourcePacks.iterator();

		while (iterator.hasNext()) {
			String s = iterator.next();

			for (ResourcePackRepository.Entry e : this.repositoryEntriesAll)
				if (e.getResourcePackName().equals(s)) {
					if (e.func_183027_f() == 1 || Settings.incompatibleResourcePacks.contains(e.getResourcePackName())) {
						this.repositoryEntries.add(e);
						break;
					}
					iterator.remove();
					logger.warn("Removed selected resource pack {} because it\'s no longer compatible", e.getResourcePackName());
				}
		}
	}

	private void fixDirResourcepacks() {
		if (this.dirResourcepacks.exists()) {
			if (!this.dirResourcepacks.isDirectory() && (!this.dirResourcepacks.delete() || !this.dirResourcepacks.mkdirs())) {
				logger.warn("Unable to recreate resourcepack folder, it exists but is not a directory: " + this.dirResourcepacks);
			}
		} else if (!this.dirResourcepacks.mkdirs()) {
			logger.warn("Unable to create resourcepack folder: " + this.dirResourcepacks);
		}
	}

	private List<File> getResourcePackFiles() {
		return this.dirResourcepacks.isDirectory() ? Arrays.asList(this.dirResourcepacks.listFiles(resourcePackFilter)) : Collections.emptyList();
	}

	public void updateRepositoryEntriesAll() {
		List<ResourcePackRepository.Entry> list = Lists.newArrayList();

		for (File file1 : this.getResourcePackFiles()) {
			ResourcePackRepository.Entry resourcepackrepository$entry = new ResourcePackRepository.Entry(file1);

			if (!this.repositoryEntriesAll.contains(resourcepackrepository$entry)) {
				try {
					resourcepackrepository$entry.updateResourcePack();
					list.add(resourcepackrepository$entry);
				} catch (Exception var6) {
					list.remove(resourcepackrepository$entry);
				}
			} else {
				int i = this.repositoryEntriesAll.indexOf(resourcepackrepository$entry);

				if (i > -1 && i < this.repositoryEntriesAll.size()) {
					list.add(this.repositoryEntriesAll.get(i));
				}
			}
		}

		this.repositoryEntriesAll.removeAll(list);

		for (ResourcePackRepository.Entry resourcepackrepository$entry1 : this.repositoryEntriesAll) {
			resourcepackrepository$entry1.closeResourcePack();
		}

		this.repositoryEntriesAll = list;
	}

	public List<ResourcePackRepository.Entry> getRepositoryEntriesAll() {
		return ImmutableList.copyOf(this.repositoryEntriesAll);
	}

	public List<ResourcePackRepository.Entry> getRepositoryEntries() {
		return ImmutableList.copyOf(this.repositoryEntries);
	}

	public void setRepositories(List<ResourcePackRepository.Entry> p_148527_1_) {
		this.repositoryEntries.clear();
		this.repositoryEntries.addAll(p_148527_1_);
	}

	public File getDirResourcepacks() {
		return this.dirResourcepacks;
	}

	public ListenableFuture<Object> downloadResourcePack(String url, String hash) {
		String s;

		if (hash.matches("^[a-f0-9]{40}$")) {
			s = hash;
		} else {
			s = "legacy";
		}

		final File file1 = new File(this.dirServerResourcepacks, s);
		this.lock.lock();

		try {
			this.func_148529_f();

			if (file1.exists() && hash.length() == 40) {
				try {
					String s1 = Hashing.sha1().hashBytes(Files.toByteArray(file1)).toString();

					if (s1.equals(hash)) {
						return this.setResourcePackInstance(file1);
					}

					logger.warn("File " + file1 + " had wrong hash (expected " + hash + ", found " + s1 + "). Deleting it.");
					FileUtils.deleteQuietly(file1);
				} catch (IOException ioexception) {
					logger.warn("File " + file1 + " couldn\'t be hashed. Deleting it.", ioexception);
					FileUtils.deleteQuietly(file1);
				}
			}

			this.func_183028_i();
			final GuiScreenWorking guiscreenworking = new GuiScreenWorking();
			Map<String, String> map = Minecraft.getSessionInfo();
			final Minecraft minecraft = Minecraft.getMinecraft();
			Futures.getUnchecked(minecraft.addScheduledTask(() -> minecraft.displayGuiScreen(guiscreenworking)));
			final SettableFuture<Object> settablefuture = SettableFuture.create();
			this.field_177322_i = HttpUtil.downloadResourcePack(file1, url, map, 52428800, guiscreenworking, minecraft.getProxy());
			Futures.addCallback(this.field_177322_i, new FutureCallback<Object>() {
				public void onSuccess(Object p_onSuccess_1_) {
					ResourcePackRepository.this.setResourcePackInstance(file1);
					settablefuture.set(null);
				}

				public void onFailure(Throwable p_onFailure_1_) {
					settablefuture.setException(p_onFailure_1_);
				}
			});
			return this.field_177322_i;
		} finally {
			this.lock.unlock();
		}
	}

	private void func_183028_i() {
		List<File> list = Lists.newArrayList(FileUtils.listFiles(this.dirServerResourcepacks, TrueFileFilter.TRUE, null));
		list.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		int i = 0;

		for (File file1 : list) {
			if (i++ >= 10) {
				logger.info("Deleting old server resource pack " + file1.getName());
				FileUtils.deleteQuietly(file1);
			}
		}
	}

	public ListenableFuture<Object> setResourcePackInstance(File p_177319_1_) {
		this.resourcePackInstance = new FileResourcePack(p_177319_1_);
		return Minecraft.getMinecraft().scheduleResourcesRefresh();
	}

	/**
	 * Getter for the IResourcePack instance associated with this ResourcePackRepository
	 */
	public IResourcePack getResourcePackInstance() {
		return this.resourcePackInstance;
	}

	public void func_148529_f() {
		this.lock.lock();

		try {
			if (this.field_177322_i != null) {
				this.field_177322_i.cancel(true);
			}

			this.field_177322_i = null;

			if (this.resourcePackInstance != null) {
				this.resourcePackInstance = null;
				Minecraft.getMinecraft().scheduleResourcesRefresh();
			}
		} finally {
			this.lock.unlock();
		}
	}

	public class Entry {

		private final File resourcePackFile;
		private IResourcePack reResourcePack;
		private PackMetadataSection rePackMetadataSection;
		private BufferedImage texturePackIcon;
		private ResourceLocation locationTexturePackIcon;

		private Entry(File resourcePackFileIn) {
			this.resourcePackFile = resourcePackFileIn;
		}

		public void updateResourcePack() throws IOException {
			this.reResourcePack = this.resourcePackFile.isDirectory() ? new FolderResourcePack(this.resourcePackFile) : new FileResourcePack(this.resourcePackFile);
			this.rePackMetadataSection = this.reResourcePack.getPackMetadata(ResourcePackRepository.this.rprMetadataSerializer, "pack");

			try {
				this.texturePackIcon = this.reResourcePack.getPackImage();
			} catch (IOException ignored) {}

			if (this.texturePackIcon == null) {
				this.texturePackIcon = ResourcePackRepository.this.rprDefaultResourcePack.getPackImage();
			}

			this.closeResourcePack();
		}

		public void bindTexturePackIcon(TextureManager textureManagerIn) {
			if (this.locationTexturePackIcon == null) {
				this.locationTexturePackIcon = textureManagerIn.getDynamicTextureLocation("texturepackicon", new DynamicTexture(this.texturePackIcon));
			}

			textureManagerIn.bindTexture(this.locationTexturePackIcon);
		}

		public void closeResourcePack() {
			if (this.reResourcePack instanceof Closeable) {
				IOUtils.closeQuietly((Closeable) this.reResourcePack);
			}
		}

		public IResourcePack getResourcePack() {
			return this.reResourcePack;
		}

		public String getResourcePackName() {
			return this.reResourcePack.getPackName();
		}

		public String getTexturePackDescription() {
			return this.rePackMetadataSection == null ? EnumChatFormatting.RED + "Invalid pack.mcmeta (or missing \'pack\' section)" : this.rePackMetadataSection.getPackDescription().getFormattedText();
		}

		public int func_183027_f() {
			return this.rePackMetadataSection.getPackFormat();
		}

		public boolean equals(Object p_equals_1_) {
			return this == p_equals_1_ || p_equals_1_ instanceof Entry && this.toString().equals(p_equals_1_.toString());
		}

		public int hashCode() {
			return this.toString().hashCode();
		}

		public String toString() {
			return String.format("%s:%s:%d", this.resourcePackFile.getName(), this.resourcePackFile.isDirectory() ? "folder" : "zip", this.resourcePackFile.lastModified());
		}

	}

}
