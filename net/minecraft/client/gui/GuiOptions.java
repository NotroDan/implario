package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.element.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.SelectorSetting;
import net.minecraft.client.settings.Settings;
import net.minecraft.client.settings.SliderSetting;
import net.minecraft.client.settings.ToggleSetting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.EnumDifficulty;

import java.io.IOException;

public class GuiOptions extends GuiScreen implements GuiYesNoCallback {

	private final GuiScreen parent;
	private GuiButton difficultyBtn;
	private GuiLockIconButton difficultyLock;

	public TabSet tabs = new TabSet(0);

	public GuiOptions(GuiScreen parent) {
		this.parent = parent;
	}

	static {

	}

	@SuppressWarnings ("UnusedAssignment")
	public void initGui() {

		buttonList.clear();
		tabs.reset();
		tabs.y = 15;

		int x1 = width / 2 - 151, x2 = width / 2 + 1;
		int y = tabs.y;

		tabs.add("Общее",
				createButton(Settings.FOV, x1, y += 22),
				createButton(Settings.REDUCED_DEBUG_INFO, x2, y),
				createButton(Settings.RENDER_FIRE, x1, y += 22)
				);

		y = tabs.y;
		tabs.add("Графика",
				createButton(Settings.RENDER_DISTANCE, x1, y += 22),
				createButton(Settings.FBO_ENABLE, x2, y).updateGraphics(),
				createButton(Settings.USE_VBO, x1, y += 22).updateGraphics(),
				createButton(Settings.VIEW_BOBBING, x2, y),
				createButton(Settings.FRAMERATE_LIMIT, x1, y += 22).updateGraphics(),
				createButton(Settings.FAST_RENDER, x2, y).updateGraphics(),
				createButton(Settings.AA_LEVEL, x1, y += 22).updateGraphics(),
				createButton(Settings.AO_LEVEL, x2, y).updateGraphics(),
				createButton(Settings.AF_LEVEL, x1, y += 22).updateGraphics(),
				createButton(Settings.LAZY_CHUNK_LOADING, x2, y).updateGraphics(),
				createButton(Settings.DYNAMIC_LIGHTS, x1, y += 22).updateGraphics(),
				createButton(Settings.CHUNK_UPDATES_DYNAMIC, x2, y).updateGraphics(),
				createButton(Settings.DYNAMIC_FOV, x1, y += 22),
				createButton(Settings.MIPMAP_LEVELS, x2, y).updateGraphics(),
				createButton(Settings.MIPMAP_TYPE, x1, y += 22).updateGraphics()
//				createButton(Settings., x1, y += 22),
//				createButton(Settings., x2, y),
//				createButton(Settings., x1, y += 22),
//				createButton(Settings., x2, y),
//				createButton(Settings., x1, y += 22),
//				createButton(Settings., x2, y),
//				createButton(Settings., x1, y += 22),
//				createButton(Settings., x2, y),
				);

		y = tabs.y;
		tabs.add("Интерфейс",
				createButton(Settings.FANCY_BUTTONS, x1, y += 22),
				createButton(Settings.RAINBOW_SHIT, x2, y),
				createButton(Settings.GUI_SCALE, x1, y += 22)
				);

		y = tabs.y;
		tabs.add("Чат",
				createButton(Settings.CHAT_VISIBILITY, x1, y += 22),
				createButton(Settings.CHAT_LINKS, x2, y),
				createButton(Settings.CHAT_COLOR, x1, y += 22),
				createButton(Settings.CHAT_LINKS_PROMPT, x2, y),
				createButton(Settings.CHAT_HEIGHT_FOCUSED, x1, y += 22),
				createButton(Settings.CHAT_SCALE, x2, y),
				createButton(Settings.CHAT_HEIGHT_UNFOCUSED, x1, y += 22),
				createButton(Settings.CHAT_WIDTH, x2, y),
				createButton(Settings.CHAT_OPACITY, x1, y + 22)
				);

		y = tabs.y;
		tabs.add("Скин",
				createButton(Settings.MODEL_CAPE, x1, y += 22),
				createButton(Settings.MODEL_HAT, x1, y += 22),
				createButton(Settings.MODEL_JACKET, x1, y += 22),
				createButton(Settings.MODEL_RIGHT_SLEEVE, x1, y += 22),
				createButton(Settings.MODEL_LEFT_SLEEVE, x1, y += 22),
				createButton(Settings.MODEL_RIGHT_PANTS_LEG, x1, y += 22),
				createButton(Settings.MODEL_LEFT_PANTS_LEG, x1, y + 22)
				);

		y = tabs.y + 22;
		x1 = width / 2 - 162;
		int x3 = width  / 2 - 192;
		tabs.add("Анимация",
				new IconButton(Settings.ANIMATED_LAVA, x1, y, new ItemStack(Items.lava_bucket))
						.setHoverText("§eАнимация лавы", "Включить/выключить анимацию", "течения лавы."),
				new IconButton(Settings.ANIMATED_WATER, x1 += 65, y, new ItemStack(Items.water_bucket))
						.setHoverText("§eАнимация воды", "Включить/выключить анимацию", "течения воды."),
				new IconButton(Settings.ANIMATED_REDSTONE, x1 += 65, y, new ItemStack(Items.redstone))
						.setHoverText("§eАнимация редстоуна", "Включить/выключить анимацию", "Красной пыли, находящейся", "Под напряжением."),
				new IconButton(Settings.ANIMATED_EXPLOSION, x1 += 65, y, new ItemStack(Blocks.tnt))
						.setHoverText("§eАнимация взрывов", "Включить/выключить отображение взрывов"),
				new IconButton(Settings.ANIMATED_PORTAL, x1 + 65, y, new ItemStack(Blocks.obsidian))
						.setHoverText("§eАнимация порталов", "Включить/выключить анимированные", "волны на текстуре порталов в ад"),
				new IconButton(Settings.ANIMATED_FIRE, x1 = width / 2 - 162, y += 65, new ItemStack(Blocks.torch))
						.setHoverText("§eАнимация огня", "Включить/выключить анимацию", "горящих блоков"),
				new IconButton(Settings.ANIMATED_SMOKE, x1 += 65, y, new ItemStack(Items.dye, 1, 15))
						.setHoverText("§eАнимация дыма", "Включить/выключить дым"),
				new IconButton(Settings.ANIMATED_FLAME, x1 += 65, y, new ItemStack(Items.flint_and_steel))
						.setHoverText("§eАнимация горения", "Включить/выключить анимацию", "горящих сущностей"),
				new IconButton(Settings.ANIMATED_TERRAIN, x1 += 65, y, new ItemStack(Blocks.grass))
						.setHoverText("§eАнимация окружения", "Включить/выключить живую природу"),
				new IconButton(Settings.ANIMATED_TEXTURES, x1 + 65, y, new ItemStack(Items.painting))
						.setHoverText("§eАнимированные текстуры", "Включить/выключить GIF-текстуры", "Распространяется только на Ресурс-паки"),
				new IconButton(Settings.VOID_PARTICLES, x3, y += 65, new ItemStack(Items.coal)).setHoverText("§eЧастицы на низкой высоте"),
				new IconButton(Settings.PORTAL_PARTICLES, x3 += 65, y, new ItemStack(Blocks.stained_glass, 2)).setHoverText("§eЧастицы порталов"),
				new IconButton(Settings.POTION_PARTICLES, x3 += 65, y, new ItemStack(Items.potionitem, 1, 17)).setHoverText("§eЧастицы зелий"),
				new IconButton(Settings.FIREWORK_PARTICLES, x3 += 65, y, new ItemStack(Items.fireworks)).setHoverText("§eСлед фейерверков"),
				new IconButton(Settings.WATER_PARTICLES, x3 += 65, y, new ItemStack(Items.water_bucket)).setHoverText("§eБрызги воды"),
				new IconButton(Settings.PARTICLES, x3 += 65, y, new ItemStack(Items.dye, 15)).setHoverText("§eОстальные частицы")
				);

		x1 = width / 2 - 116;
		y = tabs.y + 42;
		tabs.add("Звуки",
				new VolumeSlider(Settings.SOUND_MASTER, x1, y),
				new VolumeSlider(Settings.SOUND_AMBIENT, x1 += 28, y),
				new VolumeSlider(Settings.SOUND_ANIMALS, x1 += 28, y),
				new VolumeSlider(Settings.SOUND_BLOCKS, x1 += 28, y),
				new VolumeSlider(Settings.SOUND_MOBS, x1 += 28, y),
				new VolumeSlider(Settings.SOUND_MUSIC, x1 += 28, y),
				new VolumeSlider(Settings.SOUND_RECORDS, x1 += 28, y),
				new VolumeSlider(Settings.SOUND_WEATHER, x1 += 28, y),
				new VolumeSlider(Settings.SOUND_PLAYERS, x1 += 28, y)
				);

		tabs.add("Управление");


		tabs.init(buttonList, width);
		if (tabs.current >= 0) tabs.select(tabs.current);
		else tabs.select(0);


		boolean b = true;
		if (b) return;


		if (this.mc.theWorld != null) {
			EnumDifficulty enumdifficulty = this.mc.theWorld.getDifficulty();
			this.difficultyBtn = new GuiButton(108, this.width / 2 - 155, this.height / 6 - 12 + 0, 150, 20, this.func_175355_a(enumdifficulty));
			this.buttonList.add(this.difficultyBtn);

			if (this.mc.isSingleplayer() && !this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
				this.difficultyBtn.setWidth(this.difficultyBtn.getButtonWidth() - 20);
				this.difficultyLock = new GuiLockIconButton(109, this.difficultyBtn.xPosition + this.difficultyBtn.getButtonWidth(), this.difficultyBtn.yPosition);
				this.buttonList.add(this.difficultyLock);
				this.difficultyLock.func_175229_b(this.mc.theWorld.getWorldInfo().isDifficultyLocked());
				this.difficultyLock.enabled = !this.difficultyLock.isLocked();
				this.difficultyBtn.enabled = !this.difficultyLock.isLocked();
			} else this.difficultyBtn.enabled = false;
		}

		this.buttonList.add(new GuiButton(110, this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.format("options.skinCustomisation")));
		this.buttonList.add(new GuiButton(990, this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, "Вид кнопок"));
		//        this.buttonList.add(new GuiButton(8675309, this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, "Super Secret Settings...")
		//        {
		//            public void playPressSound(SoundHandler soundHandlerIn)
		//            {
		//                SoundEventAccessorComposite composite = soundHandlerIn.getRandomSoundFromCategories
		//						(SoundCategory.ANIMALS, SoundCategory.BLOCKS, SoundCategory.MOBS, SoundCategory.PLAYERS, SoundCategory.WEATHER);
		//
		//                if (composite != null)
		//                {
		//                    soundHandlerIn.playSound(PositionedSoundRecord.create(composite.getSoundEventLocation(), 0.5F));
		//                }
		//            }
		//        });
		this.buttonList.add(new GuiButton(106, this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.sounds")));
		this.buttonList.add(new GuiButton(107, this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.stream")));
		this.buttonList.add(new GuiButton(101, this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.video")));
		this.buttonList.add(new GuiButton(100, this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.controls")));
		this.buttonList.add(new GuiButton(102, this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.language")));
		this.buttonList.add(new GuiButton(103, this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.chat.title")));
		this.buttonList.add(new GuiButton(105, this.width / 2 - 155, this.height / 6 + 144 - 6, 150, 20, I18n.format("options.resourcepack")));
		//        this.buttonList.add(new GuiButton(104, this.width / 2 + 5, this.height / 6 + 144 - 6, 150, 20, I18n.format("options.snooper.view")));
		this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
	}

	private SettingButton createButton(Settings s, int x, int y) {
		if (s.getBase() instanceof ToggleSetting) return new SettingButton(s, x, y, 150, 20);
		if (s.getBase() instanceof SelectorSetting) return new SettingButton(s, x, y, 150, 20);
		if (s.getBase() instanceof SliderSetting) return new SettingSlider(x, y, s);
		throw new IllegalArgumentException("Что вы вообще делали?");
	}

	public String func_175355_a(EnumDifficulty p_175355_1_) {
		IChatComponent ichatcomponent = new ChatComponentText("");
		ichatcomponent.appendSibling(new ChatComponentTranslation("options.difficulty"));
		ichatcomponent.appendText(": ");
		ichatcomponent.appendSibling(new ChatComponentTranslation(p_175355_1_.getDifficultyResourceKey()));
		return ichatcomponent.getFormattedText();
	}

	public void confirmClicked(boolean result, int id) {
		this.mc.displayGuiScreen(this);

		if (id == 109 && result && this.mc.theWorld != null) {
			this.mc.theWorld.getWorldInfo().setDifficultyLocked(true);
			this.difficultyLock.func_175229_b(true);
			this.difficultyLock.enabled = false;
			this.difficultyBtn.enabled = false;
		}
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (!button.enabled || !button.visible) return;

		if (button.id >= 1000) {
			if (button.id == 1007) {
				mc.displayGuiScreen(new GuiControls(this));
			}
			tabs.select(button.id - 1000);
		}

		if (button instanceof SettingButton) ((SettingButton) button).click();

		if (button.enabled == button.enabled) return;

		if (button.id == 108) {
			this.mc.theWorld.getWorldInfo().setDifficulty(EnumDifficulty.getDifficultyEnum(this.mc.theWorld.getDifficulty().getDifficultyId() + 1));
			this.difficultyBtn.displayString = this.func_175355_a(this.mc.theWorld.getDifficulty());
		}

		if (button.id == 109)
			this.mc.displayGuiScreen(new GuiYesNo(this,
							new ChatComponentTranslation("difficulty.lock.title").getFormattedText(),
							new ChatComponentTranslation("difficulty.lock.question",
									new ChatComponentTranslation(this.mc.theWorld.getWorldInfo().getDifficulty().getDifficultyResourceKey())).getFormattedText(), 109)
									);

		if (button.id == 110) {
			Settings.saveOptions();
			this.mc.displayGuiScreen(new GuiCustomizeSkin(this));
		}

		if (button.id == 990) Settings.FANCY_BUTTONS.toggle();

		if (button.id == 8675309) this.mc.entityRenderer.activateNextShader();

		if (button.id == 101) {
			Settings.saveOptions();
			this.mc.displayGuiScreen(new GuiVideoSettings(this));
		}

		if (button.id == 100) {
			Settings.saveOptions();
			this.mc.displayGuiScreen(new GuiControls(this));
		}

		if (button.id == 102) {
			Settings.saveOptions();
			this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.getLanguageManager()));
		}

		if (button.id == 103) {
			Settings.saveOptions();
//			this.mc.displayGuiScreen(new ScreenChatOptions(this));
		}

		if (button.id == 200) {
			Settings.saveOptions();
			this.mc.displayGuiScreen(this.parent);
		}

		if (button.id == 105) {
			Settings.saveOptions();
			this.mc.displayGuiScreen(new GuiScreenResourcePacks(this));
		}

		if (button.id == 106) {
			Settings.saveOptions();
			this.mc.displayGuiScreen(new GuiScreenOptionsSounds(this));
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1) {
			Settings.saveOptions();
			Minecraft.getMinecraft().getSoundHandler().getSndManager().pauseAllSounds();
			Minecraft.getMinecraft().getSoundHandler().getSndManager().resumeAllSounds();
			super.keyTyped(typedChar, keyCode);
			return;
		}
		if (keyCode == 15) {
			tabs.select(tabs.getCurrent() + 1 >= tabs.getTabs().size() ? 0 : tabs.getCurrent() + 1);
		}
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		if (tabs.current == 4 && mc.thePlayer != null) drawPlayer(
				width / 2 + 80, 200, 80, width / 2 + 80 - mouseX, 100 - mouseY, mc.thePlayer);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void drawPlayer(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) posX, (float) posY, 100.0F);
		GlStateManager.scale((float) -scale, (float) scale, (float) scale);
//		GlStateManager.rotate(-30, 1, 0, 1);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
		float f = ent.renderYawOffset;
		float f1 = ent.rotationYaw;
		float f2 = ent.rotationPitch;
		float f3 = ent.prevRotationYawHead;
		float f4 = ent.rotationYawHead;
		GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
//		GlStateManager.rotate(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		ent.renderYawOffset = mouseX % 360.0F;
//		ent.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
		ent.rotationYaw = ent.renderYawOffset;
//		ent.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
		ent.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
		ent.rotationYawHead = ent.rotationYaw;
		ent.prevRotationYawHead = ent.rotationYaw;
		GlStateManager.translate(0.0F, 0.0F, 0.0F);
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		rendermanager.setPlayerViewY(180.0F);
		rendermanager.setRenderShadow(false);
		rendermanager.renderEntityWithPosYaw(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		rendermanager.setRenderShadow(true);
		ent.renderYawOffset = f;
		ent.rotationYaw = f1;
		ent.rotationPitch = f2;
		ent.prevRotationYawHead = f3;
		ent.rotationYawHead = f4;
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

	}

}
