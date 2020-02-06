package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.logging.Log;
import net.minecraft.util.StringTranslate;

import java.io.IOException;
import java.util.*;

public class LanguageManager implements IResourceManagerReloadListener {

	private static final Log logger = Log.MAIN;
	private final IMetadataSerializer theMetadataSerializer;
	private String currentLanguage;
	protected static final Locale currentLocale = new Locale();
	private Map<String, Language> languageMap = new HashMap<>();

	public LanguageManager(IMetadataSerializer theMetadataSerializerIn, String currentLanguageIn) {
		this.theMetadataSerializer = theMetadataSerializerIn;
		this.currentLanguage = currentLanguageIn;
		Lang.setLocale(currentLocale);
	}

	public void parseLanguageMetadata(List<IResourcePack> p_135043_1_) {
		this.languageMap.clear();

		for (IResourcePack iresourcepack : p_135043_1_) {
			try {
				LanguageMetadataSection languagemetadatasection = iresourcepack.getPackMetadata(this.theMetadataSerializer, "language");

				if (languagemetadatasection != null) {
					for (Language language : languagemetadatasection.getLanguages()) {
						if (!this.languageMap.containsKey(language.getLanguageCode())) {
							this.languageMap.put(language.getLanguageCode(), language);
						}
					}
				}
			} catch (RuntimeException runtimeexception) {
				logger.warn(("Unable to parse metadata section of resourcepack: " + iresourcepack.getPackName()), runtimeexception);
			} catch (IOException ioexception) {
				logger.warn(("Unable to parse metadata section of resourcepack: " + iresourcepack.getPackName()), ioexception);
			}
		}
	}

	public void onResourceManagerReload(IResourceManager resourceManager) {
		List<String> list = Collections.singletonList("ru_RU");

		if (!"ru_RU".equals(this.currentLanguage)) {
			list.add(this.currentLanguage);
		}

		currentLocale.loadLocaleDataFiles(resourceManager, list);
		StringTranslate.replaceWith(currentLocale.properties);
	}

	public boolean isCurrentLocaleUnicode() {
		return currentLocale.isUnicode();
	}

	public void setCurrentLanguage(Language currentLanguageIn) {
		this.currentLanguage = currentLanguageIn.getLanguageCode();
	}

	public Language getCurrentLanguage() {
		return this.languageMap.containsKey(this.currentLanguage) ? this.languageMap.get(this.currentLanguage) : this.languageMap.get("ru_RU");
	}

	public SortedSet<Language> getLanguages() {
		return Sets.newTreeSet(this.languageMap.values());
	}

}
