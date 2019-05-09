package net.minecraft.util.chat;

import net.minecraft.util.chat.event.ClickEvent;
import net.minecraft.util.chat.event.HoverEvent;
import net.minecraft.util.IChatComponent;

public class ChatComponentBuilder {

	private String text;
	private ClickEvent click;
	private HoverEvent hover;
	private boolean underlined;
	private String translate;

	public ChatComponentBuilder(String s) {
		text = s;
	}

	public ChatComponentBuilder click(ClickEvent.Action action, String value) {
		click = new ClickEvent(action, value);
		return this;
	}
	public ChatComponentBuilder hover(HoverEvent.Action action, IChatComponent value) {
		hover = new HoverEvent(action, value);
		return this;
	}
	public ChatComponentBuilder hover(HoverEvent.Action action, String text) {
		hover = new HoverEvent(action, new ChatComponentText(text));
		return this;
	}
	public ChatComponentBuilder underline() {
		underlined = true;
		return this;
	}
	public ChatComponentBuilder translate(String s) {
		translate = s;
		return this;
	}

	public IChatComponent build() {
		ChatComponentText c = new ChatComponentText(text);
		if (click != null) c.getChatStyle().setChatClickEvent(click);
		if (hover != null) c.getChatStyle().setChatHoverEvent(hover);
		c.getChatStyle().setUnderlined(underlined);
		return translate == null ? c : new ChatComponentTranslation(translate, c);
	}
}
