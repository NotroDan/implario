package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.Log;
import net.minecraft.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.element.ChatLine;
import net.minecraft.client.renderer.G;
import net.minecraft.client.settings.Settings;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

import java.util.Iterator;
import java.util.List;

public class GuiNewChat extends Gui {

	private static final Logger logger = Logger.getInstance();
	private final Minecraft mc;
	private final List<String> sentMessages = Lists.newArrayList();
	private final List<ChatLine> chatLines = Lists.newArrayList();
	private final List<ChatLine> field_146253_i = Lists.newArrayList();
	private int scrollPos;
	private boolean isScrolled;

	public GuiNewChat(Minecraft mcIn) {
		this.mc = mcIn;
	}

	public void drawChat(int p_146230_1_) {
		if (Settings.CHAT_VISIBILITY.i() == 2) return;
		int i = this.getLineCount();
		boolean flag = false;
		int j = 0;
		int k = this.field_146253_i.size();
		float f = Settings.CHAT_OPACITY.f() * 0.9F + 0.1F;

		if (k > 0) {
			if (this.getChatOpen()) {
				flag = true;
			}

			float f1 = this.getChatScale();
			int l = MathHelper.ceiling_float_int((float) this.getChatWidth() / f1);
			G.pushMatrix();
			G.translate(2.0F, 20.0F, 0.0F);
			G.scale(f1, f1, 1.0F);

			for (int i1 = 0; i1 + this.scrollPos < this.field_146253_i.size() && i1 < i; ++i1) {
				ChatLine chatline = this.field_146253_i.get(i1 + this.scrollPos);

				if (chatline != null) {
					int j1 = p_146230_1_ - chatline.getUpdatedCounter();

					if (j1 < 200 || flag) {
						double d0 = (double) j1 / 200.0D;
						d0 = 1.0D - d0;
						d0 = d0 * 10.0D;
						d0 = MathHelper.clamp_double(d0, 0.0D, 1.0D);
						d0 = d0 * d0;
						int l1 = (int) (255.0D * d0);

						if (flag) {
							l1 = 255;
						}

						l1 = (int) ((float) l1 * f);
						++j;

						if (l1 > 3) {
							int i2 = 0;
							int j2 = -i1 * 9;
							drawRect(i2, j2 - 9, i2 + l + 4, j2, l1 / 2 << 24);
							String s = chatline.getChatComponent().getFormattedText();
							G.enableBlend();
							this.mc.fontRendererObj.drawStringWithShadow(s, (float) i2, (float) (j2 - 8), 16777215 + (l1 << 24));
							G.disableAlpha();
							G.disableBlend();
						}
					}
				}
			}

			if (flag) {
				int k2 = this.mc.fontRendererObj.FONT_HEIGHT;
				G.translate(-3.0F, 0.0F, 0.0F);
				int l2 = k * k2 + k;
				int i3 = j * k2 + j;
				int j3 = this.scrollPos * i3 / k;
				int k1 = i3 * i3 / l2;

				if (l2 != i3) {
					int k3 = j3 > 0 ? 170 : 96;
					int l3 = this.isScrolled ? 13382451 : 3355562;
					drawRect(0, -j3, 2, -j3 - k1, l3 + (k3 << 24));
					drawRect(2, -j3, 1, -j3 - k1, 13421772 + (k3 << 24));
				}
			}

			G.popMatrix();
		}
	}

	/**
	 * Clears the chat.
	 */
	public void clearChatMessages() {
		this.field_146253_i.clear();
		this.chatLines.clear();
		this.sentMessages.clear();
	}

	public void printChatMessage(IChatComponent p_146227_1_) {
		this.printChatMessageWithOptionalDeletion(p_146227_1_, 0);
	}

	/**
	 * prints the ChatComponent to Chat. If the ID is not 0, deletes an existing Chat Line of that ID from the GUI
	 */
	public void printChatMessageWithOptionalDeletion(IChatComponent component, int line) {
		this.setChatLine(component, line, this.mc.ingameGUI.getUpdateCounter(), false);
		Log.CHAT.info(component.getUnformattedText().replaceAll("§.", ""));
	}

	private void setChatLine(IChatComponent p_146237_1_, int p_146237_2_, int p_146237_3_, boolean p_146237_4_) {
		if (p_146237_2_ != 0) {
			this.deleteChatLine(p_146237_2_);
		}

		int i = MathHelper.floor_float((float) this.getChatWidth() / this.getChatScale());
		List<IChatComponent> list = GuiUtilRenderComponents.func_178908_a(p_146237_1_, i, this.mc.fontRendererObj, false, false);
		boolean flag = this.getChatOpen();

		for (IChatComponent ichatcomponent : list) {
			if (flag && this.scrollPos > 0) {
				this.isScrolled = true;
				this.scroll(1);
			}

			this.field_146253_i.add(0, new ChatLine(p_146237_3_, ichatcomponent, p_146237_2_));
		}

		while (this.field_146253_i.size() > 100) {
			this.field_146253_i.remove(this.field_146253_i.size() - 1);
		}

		if (!p_146237_4_) {
			this.chatLines.add(0, new ChatLine(p_146237_3_, p_146237_1_, p_146237_2_));

			while (this.chatLines.size() > 100) {
				this.chatLines.remove(this.chatLines.size() - 1);
			}
		}
	}

	public void refreshChat() {
		this.field_146253_i.clear();
		this.resetScroll();

		for (int i = this.chatLines.size() - 1; i >= 0; --i) {
			ChatLine chatline = this.chatLines.get(i);
			this.setChatLine(chatline.getChatComponent(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
		}
	}

	public List<String> getSentMessages() {
		return this.sentMessages;
	}

	/**
	 * Adds this string to the list of sent messages, for recall using the up/down arrow keys
	 */
	public void addToSentMessages(String p_146239_1_) {
		if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(p_146239_1_)) {
			this.sentMessages.add(p_146239_1_);
		}
	}

	/**
	 * Resets the chat scroll (executed when the GUI is closed, among others)
	 */
	public void resetScroll() {
		this.scrollPos = 0;
		this.isScrolled = false;
	}

	/**
	 * Scrolls the chat by the given number of lines.
	 */
	public void scroll(int p_146229_1_) {
		this.scrollPos += p_146229_1_;
		int i = this.field_146253_i.size();

		if (this.scrollPos > i - this.getLineCount()) {
			this.scrollPos = i - this.getLineCount();
		}

		if (this.scrollPos <= 0) {
			this.scrollPos = 0;
			this.isScrolled = false;
		}
	}

	/**
	 * Gets the chat component under the mouse
	 */
	public IChatComponent getChatComponent(int p_146236_1_, int p_146236_2_) {
		if (!this.getChatOpen()) {
			return null;
		}
		ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		int i = scaledresolution.getScaleFactor();
		float f = this.getChatScale();
		int j = p_146236_1_ / i - 3;
		int k = p_146236_2_ / i - 27;
		j = MathHelper.floor_float((float) j / f);
		k = MathHelper.floor_float((float) k / f);

		if (j >= 0 && k >= 0) {
			int l = Math.min(this.getLineCount(), this.field_146253_i.size());

			if (j <= MathHelper.floor_float((float) this.getChatWidth() / this.getChatScale()) && k < this.mc.fontRendererObj.FONT_HEIGHT * l + l) {
				int i1 = k / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;

				if (i1 >= 0 && i1 < this.field_146253_i.size()) {
					ChatLine chatline = this.field_146253_i.get(i1);
					int j1 = 0;

					for (IChatComponent ichatcomponent : chatline.getChatComponent()) {
						if (ichatcomponent instanceof ChatComponentText) {
							j1 += this.mc.fontRendererObj.getStringWidth(GuiUtilRenderComponents.func_178909_a(((ChatComponentText) ichatcomponent).getChatComponentText_TextValue(), false));

							if (j1 > j) {
								return ichatcomponent;
							}
						}
					}
				}

				return null;
			}
			return null;
		}
		return null;
	}

	/**
	 * Returns true if the chat GUI is open
	 */
	public boolean getChatOpen() {
		return this.mc.currentScreen instanceof GuiChat;
	}

	/**
	 * finds and deletes a Chat line by ID
	 */
	public void deleteChatLine(int p_146242_1_) {
		Iterator<ChatLine> iterator = this.field_146253_i.iterator();

		while (iterator.hasNext()) {
			ChatLine chatline = iterator.next();

			if (chatline.getChatLineID() == p_146242_1_) {
				iterator.remove();
			}
		}

		iterator = this.chatLines.iterator();

		while (iterator.hasNext()) {
			ChatLine chatline1 = iterator.next();

			if (chatline1.getChatLineID() == p_146242_1_) {
				iterator.remove();
				break;
			}
		}
	}

	public int getChatWidth() {
		return calculateChatboxWidth(Settings.CHAT_WIDTH.f());
	}

	public int getChatHeight() {
		return calculateChatboxHeight(this.getChatOpen() ? Settings.CHAT_HEIGHT_FOCUSED.f() : Settings.CHAT_HEIGHT_UNFOCUSED.f());
	}

	/**
	 * Returns the chatscale from mc.gameSettings.chatScale
	 */
	public float getChatScale() {
		return Settings.CHAT_SCALE.f();
	}

	public static int calculateChatboxWidth(float v) {
		//		int i = 320;
		//		int j = 40;
		//		return MathHelper.floor_float(v * (float) (i - j) + (float) j);
		return (int) v;
	}

	public static int calculateChatboxHeight(float v) {
//		int i = 180;
//		int j = 20;
//		return MathHelper.floor_float(v * (float) (i - j) + (float) j);
		return (int) v;
	}

	public int getLineCount() {
		return this.getChatHeight() / 9;
	}

}