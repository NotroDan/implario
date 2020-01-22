package net.minecraft.util.chat;

import java.util.List;

import net.minecraft.command.api.ICommandSender;
import net.minecraft.command.legacy.PlayerSelector;
import net.minecraft.entity.Entity;
import net.minecraft.util.IChatComponent;

public class ChatComponentProcessor {

	public static IChatComponent processComponent(ICommandSender commandSender, IChatComponent component, Entity entityIn) {
		IChatComponent ichatcomponent = null;

		if (component instanceof ChatComponentScore) {
			ChatComponentScore chatcomponentscore = (ChatComponentScore) component;
			String s = chatcomponentscore.getName();

			if (PlayerSelector.hasArguments(s)) {
				List<Entity> list = PlayerSelector.matchEntities(commandSender, s, Entity.class);

				if (list.size() != 1) return null;

				s = ((Entity) list.get(0)).getName();
			}

			ichatcomponent = entityIn != null && s.equals("*") ? new ChatComponentScore(entityIn.getName(), chatcomponentscore.getObjective()) : new ChatComponentScore(s,
					chatcomponentscore.getObjective());
			((ChatComponentScore) ichatcomponent).setValue(chatcomponentscore.getUnformattedTextForChat());
		} else if (component instanceof ChatComponentSelector) {
			String s1 = ((ChatComponentSelector) component).getSelector();
			ichatcomponent = PlayerSelector.matchEntitiesToChatComponent(commandSender, s1);

			if (ichatcomponent == null) {
				ichatcomponent = new ChatComponentText("");
			}
		} else if (component instanceof ChatComponentText) {
			ichatcomponent = new ChatComponentText(((ChatComponentText) component).getChatComponentText_TextValue());
		} else {
			if (!(component instanceof ChatComponentTranslation)) {
				return component;
			}

			Object[] aobject = ((ChatComponentTranslation) component).getFormatArgs();

			for (int i = 0; i < aobject.length; ++i) {
				Object object = aobject[i];

				if (object instanceof IChatComponent) {
					IChatComponent recursive = processComponent(commandSender, (IChatComponent) object, entityIn);
					if (recursive == null) return null;
					aobject[i] = recursive;
				}
			}

			ichatcomponent = new ChatComponentTranslation(((ChatComponentTranslation) component).getKey(), aobject);
		}

		ChatStyle chatstyle = component.getChatStyle();

		if (chatstyle != null) {
			ichatcomponent.setChatStyle(chatstyle.createShallowCopy());
		}

		for (IChatComponent ichatcomponent1 : component.getSiblings()) {
			IChatComponent recursive = processComponent(commandSender, ichatcomponent1, entityIn);
			if (recursive == null) return null;
			ichatcomponent.appendSibling(recursive);
		}

		return ichatcomponent;
	}

}
