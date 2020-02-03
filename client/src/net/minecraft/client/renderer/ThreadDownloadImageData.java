package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.logging.Log;
import net.minecraft.util.FileUtil;
import net.minecraft.util.ResourceLocation;
import optifine.Config;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;

//TODO: надо http async, это прям неоч
public class ThreadDownloadImageData extends SimpleTexture {
	private static final ExecutorService executors = Executors.newCachedThreadPool((runnable) ->
		new Thread(runnable, "Minecraft Texture Download"));
	private static final Log logger = Log.MAIN;
	private final File cacheFile;
	private final String imageUrl;
	private final IImageBuffer imageBuffer;
	private BufferedImage bufferedImage;
	private boolean textureUploaded;

	public Boolean imageFound = null;
	public boolean pipeline = false;

	public ThreadDownloadImageData(File cacheFileIn, String imageUrlIn, ResourceLocation textureResourceLocation, IImageBuffer imageBufferIn) {
		super(textureResourceLocation);
		this.cacheFile = cacheFileIn;
		this.imageUrl = imageUrlIn;
		this.imageBuffer = imageBufferIn;
	}

	private void checkTextureUploaded() {
		if (!this.textureUploaded && this.bufferedImage != null) {
			this.textureUploaded = true;

			if (this.textureLocation != null) this.deleteGlTexture();

			TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
		}
	}

	public int getGlTextureId() {
		this.checkTextureUploaded();
		return super.getGlTextureId();
	}

	public void setBufferedImage(BufferedImage bufferedImageIn) {
		this.bufferedImage = bufferedImageIn;

		if (this.imageBuffer != null) this.imageBuffer.skinAvailable();

		this.imageFound = this.bufferedImage != null;
	}

	public void loadTexture(IResourceManager resourceManager) throws IOException {
		if (this.bufferedImage == null && this.textureLocation != null) super.loadTexture(resourceManager);

		if (this.cacheFile != null && this.cacheFile.isFile()) {
			logger.debug("Loading http texture from memory cache (" + cacheFile + ")");

			try {
				this.bufferedImage = ImageIO.read(this.cacheFile);

				if (this.imageBuffer != null) this.setBufferedImage(this.imageBuffer.parseUserSkin(this.bufferedImage));

				this.imageFound = this.bufferedImage != null;
			} catch (IOException ioexception) {
				logger.error((String) ("Couldn\'t load skin " + this.cacheFile), (Throwable) ioexception);
				this.loadTextureFromServer();
			}
		} else this.loadTextureFromServer();
	}

	protected void loadTextureFromServer() {
		executors.submit(() -> {
			HttpURLConnection httpurlconnection = null;
			logger.debug("Downloading http texture from " + imageUrl + " to " + cacheFile);

			try {
				httpurlconnection = (HttpURLConnection) new URL(imageUrl).openConnection(Minecraft.get().getProxy());
				httpurlconnection.setDoInput(true);
				httpurlconnection.setDoOutput(false);
				httpurlconnection.connect();

				if (httpurlconnection.getResponseCode() / 100 != 2) {
					if (httpurlconnection.getErrorStream() != null) Config.readAll(httpurlconnection.getErrorStream());

					return;
				}

				BufferedImage bufferedimage;

				if (cacheFile != null) {
					InputStream in = httpurlconnection.getInputStream();
					byte array[] = new byte[in.available()];
					FileUtil.readInputStream(in, array);
					FileUtils.writeByteArrayToFile(cacheFile, array);
					bufferedimage = ImageIO.read(new ByteArrayInputStream(array));
				} else bufferedimage = TextureUtil.readBufferedImage(httpurlconnection.getInputStream());

				if (imageBuffer != null) bufferedimage = imageBuffer.parseUserSkin(bufferedimage);

				setBufferedImage(bufferedimage);
			} catch (Exception exception) {
				ThreadDownloadImageData.logger.error("Couldn\'t download http texture: " + exception.getClass().getName() + ": " + exception.getMessage());
				return;
			} finally {
				if (httpurlconnection != null) httpurlconnection.disconnect();

				imageFound = bufferedImage != null;
			}
		});
	}
}
