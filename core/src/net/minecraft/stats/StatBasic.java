package net.minecraft.stats;

import net.minecraft.util.chat.ChatComponentTranslation;

public class StatBasic extends StatBase {

	public StatBasic(String key) {
		super(key, new ChatComponentTranslation(key));
	}

	public StatBasic(String key, StatFormatter type) {
		super(key, new ChatComponentTranslation(key), type);
	}

	/**
	 * Register the stat into StatList.
	 */
	public StatBase registerStat() {
		super.registerStat();
		StatList.generalStats.add(this);
		return this;
	}

}
