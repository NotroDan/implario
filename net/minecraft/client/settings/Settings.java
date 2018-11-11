package net.minecraft.client.settings;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.world.EnumDifficulty;
import optifine.ClearWater;
import optifine.Config;
import org.lwjgl.opengl.Display;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public enum Settings {

	INVERT_MOUSE("Инверсия мыши", false),
	SENSITIVITY("Чувствительность", 0f, 1f, 0.01f, 0.5f),
	FOV("Поле зрения", 30, 110, 1, 90),
	GAMMA("Гамма", 0, 1, 0.01f, 1),
	RENDER_DISTANCE("Дальность прорисовки", 2, 32, 1, 10) {
		@Override
		public void change() {
			Minecraft.getMinecraft().renderGlobal.setDisplayListEntitiesDirty();
		}
	},
	VIEW_BOBBING("Покачивание камеры", true),
	FRAMERATE_LIMIT(new FpsSetting("FRAMERATE_LIMIT")) {
		@Override
		public String getCaption() {
			return f() == 0 ? "Верт. синх." : f() == 260 ? "Неогранич." : (int) f() + " FPS";
		}
	}, //0, 260, 5, 260),
	FBO_ENABLE("Использовать FBO", false),
	GUI_SCALE("Интерфейс", 2, "Авто", "Маленький", "Обычный", "Крупный") {
		@Override
		public void change() {
			Minecraft mc = Minecraft.getMinecraft();
			ScaledResolution r = new ScaledResolution(mc);
			mc.currentScreen.setWorldAndResolution(mc, r.getScaledWidth(), r.getScaledHeight());
		}
	},
	PARTICLES("Частицы", 0, "Все", "Меньше", "Минимум"),
	CHAT_VISIBILITY("Чат", 0, "Виден", "Только команды", "Скрыт"),
	CHAT_COLOR("Цвета", true),
	CHAT_LINKS("Ссылки", true),
	CHAT_OPACITY("Непрозрачность", 0, 1, 0.01f, 1f),
	CHAT_LINKS_PROMPT("Подтверждение перехода", true),
	USE_FULLSCREEN("Полноэкранный режим", false),
	ENABLE_VSYNC("Вертикальная синхронизация", false),
	USE_VBO("Использовать VBO", false),
	CHAT_SCALE("Размер чата", 0, 1, 0.01f, 1f),
	CHAT_WIDTH("Ширина чата", 40, 600, 1, 320),
	CHAT_HEIGHT_FOCUSED("Высота (Активный чат)", 20, 300, 1, 180),
	CHAT_HEIGHT_UNFOCUSED("Высота (Неактивный чат)", 20, 300, 1, 100),
	MIPMAP_LEVELS("Уровень сглаживания", 0, 4, 1, 4) {
		public void change() {
			Minecraft mc = Minecraft.getMinecraft();
			mc.getTextureMapBlocks().setMipmapLevels(i());
			mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			mc.getTextureMapBlocks().setBlurMipmapDirect(false, i() > 0);
			mc.scheduleResourcesRefresh();
		}
	},
	FORCE_UNICODE_FONT("Шрифт Unicode", true),
	USE_NATIVE_CONNECTION("Нативное соединение", true),
	BLOCK_ALTERNATIVES("Альтернативные блоки", false),
	REDUCED_DEBUG_INFO("Экран F3", 1, "§aПолный", "§eСокращённый", "§6Только FPS"),
	ENTITY_SHADOWS("Тени сущностей", true),
	FOG_FANCY("Качество тумана", 1, "§cВыкл", "Быстро", "Детально"),
	FOG_START("Дальность тумана", 0.2f, 0.8f, 0.2f, 0.8f),
	MIPMAP_TYPE("Тип сглаживания", 0, "§aN", "§eL", "§6BL", "§cTL"),
	SMOOTH_FPS("Стабилизация FPS", false),
	CLOUDS("Облака", 0, "§cВыкл", "Быстро", "Детально"),
	CLOUD_HEIGHT("Высота облаков", 0, 1, 0.01f, 0f),
	TREES("Листва", 0, "Быстро", "Детально"),
	RAIN("Погода", 1, "§cВыкл", "Быстро", "Детально"),
	ANIMATED_WATER("Вода", true),
	ANIMATED_LAVA("Лава", true),
	ANIMATED_FIRE("Огонь", true),
	ANIMATED_PORTAL("Портал", true),
	AO_LEVEL("Мягкое освещение", 0, 1, 0.01f, 1f),
	LAGOMETER("Лагометр", false),
	BETTER_GRASS("Улучшенная трава", 0, "§cВыкл.", "Быстро", "Детально"),
	ANIMATED_REDSTONE("Редстоун", true),
	ANIMATED_EXPLOSION("Взрывы", true),
	ANIMATED_FLAME("Пламя", true),
	ANIMATED_SMOKE("Дым", true),
	WEATHER("Погода", true),
	SKY("Небо", true),
	STARS("Звёзды", true),
	SUN_MOON("Солнце и луна", true),
	VIGNETTE("Виньетирование", 2, "Быстрое", "Качественное"),
	CHUNK_UPDATES("Обновление чанков (CU)", 1, 5, 1, 1),
	CHUNK_UPDATES_DYNAMIC("Динамическое CU", false),
	TIME("Тема", 0, "§cВыкл.", "Светлая", "Тёмная"),
	CLEAR_WATER("Чистая вода", true),
	SMOOTH_WORLD("Стабилизация мира", false),
	VOID_PARTICLES("Пустота", true),
	WATER_PARTICLES("Брызги", true),
	PORTAL_PARTICLES("Портал", true),
	POTION_PARTICLES("Зелья", true),
	FIREWORK_PARTICLES("Ракеты", true),
	PROFILER("Профайлер", false),
	DRIPPING_WATER_LAVA("Капли лавы/воды", true),
	BETTER_SNOW("Улучшенный снег", false),
	ANIMATED_TERRAIN("Окружение", true),
	SWAMP_COLORS("Болотные цвета", true),
	RANDOM_MOBS("Случайные мобы", false),
	SMOOTH_BIOMES("Гладкие биомы", true),
	CUSTOM_FONTS("Кастомные шрифты", true),
	CUSTOM_COLORS("Кастомные цвета", true),
	SHOW_CAPES("Отображать плащи", true),
	CONNECTED_TEXTURES("Соединение текстур", true),
	CUSTOM_ITEMS("Кастомные предметы", true),
	AA_LEVEL("Антиалиасинг", 0, 16, 2, 0),
	AF_LEVEL("Анизотропная фильтрация", 0, 16, 4, 0),
	ANIMATED_TEXTURES("Текстуры", true),
	NATURAL_TEXTURES("Природные текстуры", true),
	HELD_ITEM_TOOLTIPS("Подсказки о предмете в руке", true),
	DROPPED_ITEMS("3D-Предметы", true),
	LAZY_CHUNK_LOADING("Ленивая загрузка чанков", false),
	CUSTOM_SKY("Кастомное небо", true),
	FAST_RENDER("Быстрый рендер", true),
	TRANSLUCENT_BLOCKS("Просвечивание блоков", 0, "Быстрое", "Детальное"),
	DYNAMIC_FOV("Динамика поля зрения", true),
	DYNAMIC_LIGHTS("Динамическое освещение", true),
	SOUND_MASTER("Общая громкость"),
	SOUND_MUSIC("Музыка"),
	SOUND_RECORDS("Пластинки"),
	SOUND_WEATHER("Погода"),
	SOUND_BLOCKS("Блоки"),
	SOUND_MOBS("Мобы"),
	SOUND_ANIMALS("Животные"),
	SOUND_PLAYERS("Игроки"),
	SOUND_AMBIENT("Окружение"),
	MODEL_CAPE("Плащ"),
	MODEL_JACKET("Куртка"),
	MODEL_LEFT_SLEEVE("Левый рукав"),
	MODEL_RIGHT_SLEEVE("Правый рукав"),
	MODEL_LEFT_PANTS_LEG("Левая штанина"),
	MODEL_RIGHT_PANTS_LEG("Правая штанина"),
	MODEL_HAT("Шапка"),
	SMOOTH_CAMERA("Плавная камера", false),
	HIDE_GUI("Скрыть GUI", false),
	SHOW_DEBUG("Показывать экран отладки", false),
	PERSPECTIVE("Перспектива", 0, "От первого лица", "От третьего лица", "От второго лица"),
	PAUSE_FOCUS("Пауза при сворачивании", true),
	ITEM_TOOLTIPS("ID предметов", false),
	FANCY_BUTTONS("Тип кнопок", true),
	RAINBOW_SHIT("Радужная хуйня", true),
	HIDE_SERVER_ADDRESS("Скрыть IP серверов", false),
	RENDER_FIRE("Огонь", 0, "Стандартный", "Иконка", "Отключён"),
	SUDOKU_SEPARATORS("Разделители", false),
	FAST_PLACE("Быстрый ПКМ", false);

	private static final Settings[] SOUNDS;
	private static final Settings[] MODELPARTS;
	public static List<String> resourcePacks = new ArrayList<>();
	public static List<String> incompatibleResourcePacks = new ArrayList<>();
	public static EnumDifficulty difficulty = EnumDifficulty.NORMAL;
	public static String lastServer = "";
	public static String language = "ru_RU";

	private static final Gson gson = new Gson();
	private static final ParameterizedType gsonType = new ParameterizedType() {
		public Type[] getActualTypeArguments() {return new Type[] {String.class};}

		public Type getRawType() {return List.class;}

		public Type getOwnerType() {return null;}
	};

	static {
		List<Settings> sounds = new ArrayList<>();
		List<Settings> modelparts = new ArrayList<>();
		for (Settings setting : values()) {
			if (setting.name().startsWith("SOUND_")) sounds.add(setting);
			if (setting.name().startsWith("MODEL_")) modelparts.add(setting);
		}
		SOUNDS = sounds.toArray(new Settings[0]);
		MODELPARTS = modelparts.toArray(new Settings[0]);
	}

	private final Setting base;
	private SoundCategory soundCategory;

	Settings(String caption, float min, float max, float step, float defaultValue) {
		base = new SliderSetting(name(), caption, min, max, defaultValue, step);
	}
	Settings(String caption, int defaultState, String... variants) {
		base = new SelectorSetting(name(), caption, defaultState, variants);
	}
	Settings(String caption, boolean defaultState) {
		base = new ToggleSetting(name(), caption, defaultState);
	}
	Settings(String caption) {
		if (name().startsWith("SOUND_")) {
			base = new SliderSetting(name(), caption, 0, 1, 0.1f, 0.01f);
			soundCategory = SoundCategory.valueOf(name().substring(6));
		}
		else if (name().startsWith("MODEL_")) base = new ToggleSetting(name(), caption, true);
		else throw new IllegalArgumentException();
	}

	Settings(FpsSetting setting) {
		base = setting;
	}


	public static void init() {
		loadOptions();
		Config.initGameSettings();
	}

	public static void loadOptions() {
		try {
			File file = new File("settings.txt");
			if (!file.exists()) return;
			BufferedReader bufferedreader = new BufferedReader(new FileReader(file));
			String s;
			while ((s = bufferedreader.readLine()) != null) {
				try {

					if (s.startsWith("key_")) {
						String[] args = s.split(": ");
						KeyBinding key = KeyBinding.valueOf(args[0].substring(4));
						key.setKeyCode(Integer.parseInt(args[1]));
					} else if (s.startsWith("resourcePacks: ")) {
						resourcePacks = gson.fromJson(s.substring(s.indexOf(58) + 2), gsonType);
						if (resourcePacks == null) resourcePacks = Lists.newArrayList();
					} else if (s.startsWith("incompatibleResourcePacks: ")) {
						incompatibleResourcePacks = gson.fromJson(s.substring(s.indexOf(0x3a) + 2), gsonType);
						if (incompatibleResourcePacks == null) incompatibleResourcePacks = Lists.newArrayList();
					} else if (s.startsWith("lastServer")) {
						lastServer = s.substring(s.indexOf(58) + 2);
					} else if (s.startsWith("lang")) {
						String[] args = s.split(": ");
						language = args[1];
					} else Setting.fromString(s);

				} catch (Exception exception) {
					System.out.println("Некорректная опция: " + s);
					exception.printStackTrace();
				}
			}
			KeyBinding.resetKeyBindingArrayAndHash();
			bufferedreader.close();
		} catch (Exception ex) {
			System.err.println("Не удалось загрузить опции.");
			ex.printStackTrace();
		}

	}

	public static void saveOptions() {
		try {
			PrintWriter w = new PrintWriter(new FileWriter(new File("settings.txt")));

			for (KeyBinding key : KeyBinding.values()) w.println("key_" + key.name() + ": " + key.getKeyCode());
			w.println("resourcePacks: " + gson.toJson(resourcePacks));
			w.println("incompatibleResourcePacks: " + gson.toJson(incompatibleResourcePacks));
			w.println("lastServer: " + lastServer);
			w.println("lang: " + language);
			for (Settings s : values()) w.println(s.base.toString());
			w.close();
		} catch (Exception exception) {
			System.out.println("Не удалось сохранить опции.");
			exception.printStackTrace();
		}
	}

	public static void updateVSync() {
		Display.setVSyncEnabled(ENABLE_VSYNC.b());
	}

	public static void updateWaterOpacity() {
		if (Minecraft.getMinecraft().isIntegratedServerRunning() && Minecraft.getMinecraft().getIntegratedServer() != null) Config.waterOpacityChanged = true;
		ClearWater.updateWaterOpacity(Minecraft.getMinecraft().theWorld);
	}


	public static void setSoundLevel(SoundCategory category, float level) {
		valueOf("SOUND_" + category.name()).set(level);
		Minecraft.getMinecraft().getSoundHandler().setSoundLevel(category, level);
	}
	public static float getSoundLevel(SoundCategory category) {
		try {
			return valueOf("SOUND_" + category.name()).f();
		} catch (IllegalArgumentException e) {
			return 0;
		}
	}

	public static void sendSettingsToServer() {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer == null) return;
		int i = 0;
		for (Settings modelpart : MODELPARTS) i |= EnumPlayerModelParts.valueOf(modelpart.name().substring(6)).getPartMask();
		mc.thePlayer.sendQueue.addToSendQueue(new C15PacketClientSettings(language, RENDER_DISTANCE.i(),
				EntityPlayer.EnumChatVisibility.values()[CHAT_VISIBILITY.i()], CHAT_COLOR.b(), i));
	}

	public static void resetSettings() {
		for (Settings s : Settings.values()) s.base.reset();
		saveOptions();
	}

	public static void setAllAnimations(boolean b) {
		for (Settings s : values()) if (s.name().contains("ANIMATED") || s.name().contains("PARTICLE")) s.set(b);
	}

	public float f() {
		return base.floatValue();
	}

	public int i() {
		return base instanceof SelectorSetting ? ((SelectorSetting) base).state : (int) base.floatValue();
	}

	public boolean b() {
		return base.booleanValue();
	}

	public Setting getBase() {
		return base;
	}

	public void set(boolean b) {
		base.set(b);
	}
	public void set(float f) {
		base.set(f);
	}
	public boolean toggle() {
		return ((ToggleSetting) base).toggle();
	}

	public static int getPerspective() {
		return PERSPECTIVE.i();
	}

	public static void setModelPart(EnumPlayerModelParts part, boolean enabled) {
		valueOf("MODEL_" + part.name()).set(enabled);
		sendSettingsToServer();
	}
	public static void toggleModelPart(EnumPlayerModelParts part) {
		valueOf("MODEL_" + part.name()).toggle();
		sendSettingsToServer();
	}
	public static Settings getModelPart(EnumPlayerModelParts part) {
		return valueOf("MODEL_" + part.name());
	}

	public void change() {
		if (name().startsWith("CHAT")) Minecraft.getMinecraft().ingameGUI.getChatGUI().refreshChat();
		if (name().startsWith("SOUND_")) Minecraft.getMinecraft().getSoundHandler().setSoundLevel(soundCategory, f());
	}

	public String getCaption() {
		return null;
	}

	public SoundCategory getSoundCategory() {
		return soundCategory;
	}
}
