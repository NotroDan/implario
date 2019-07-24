package net.minecraft.stats;

import lombok.*;
import lombok.experimental.Accessors;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.chat.ChatComponentText;
import net.minecraft.util.chat.event.HoverEvent;

@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StatBase {

	public final String statId;
	private final IChatComponent statName;
	private final StatFormatter type;

	@Getter
	private final IScoreObjectiveCriteria criteria;

	public boolean independent;

	@ToString.Exclude
	@Accessors(fluent = true, chain = true)
	@Getter @Setter
	private Class<? extends IJsonSerializable> serializer;

	public StatBase(String id, IChatComponent name, StatFormatter formatter) {
		this(id, name, formatter, new ObjectiveStat(id));
//		IScoreObjectiveCriteria.INSTANCES.put(this.criteria.getName(), this.criteria);
	}

	public StatBase(String statIdIn, IChatComponent statNameIn) {
		this(statIdIn, statNameIn, StatFormatter.simpleFormat);
	}

	/**
	 * Initializes the current stat as independent (i.e., lacking prerequisites for being updated) and returns the
	 * current instance.
	 */
	public StatBase indepenpent() {
		this.independent = true;
		return this;
	}

	/**
	 * Register the stat into StatList.
	 */
	public StatBase registerStat() {
		if (StatList.oneShotStats.containsKey(this.statId))
			throw new RuntimeException("Duplicate stat id: \"" + StatList.oneShotStats.get(this.statId).statName + "\" and \"" + this.statName + "\" at id " + this.statId);
		StatList.allStats.add(this);
		StatList.oneShotStats.put(this.statId, this);
		return this;
	}

	/**
	 * Returns whether or not the StatBase-derived class is a statistic (running counter) or an achievement (one-shot).
	 */
	public boolean isAchievement() {
		return false;
	}

	public String format(int value) {
		return this.type.format(value);
	}

	public IChatComponent getStatName() {
		IChatComponent ichatcomponent = this.statName.createCopy();
		ichatcomponent.getChatStyle().setColor(EnumChatFormatting.GRAY);
		ichatcomponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT, new ChatComponentText(this.statId)));
		return ichatcomponent;
	}

	public IChatComponent func_150955_j() {
		IChatComponent ichatcomponent = this.getStatName();
		IChatComponent ichatcomponent1 = new ChatComponentText("[").appendSibling(ichatcomponent).appendText("]");
		ichatcomponent1.setChatStyle(ichatcomponent.getChatStyle());
		return ichatcomponent1;
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		}
		if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
			StatBase statbase = (StatBase) p_equals_1_;
			return this.statId.equals(statbase.statId);
		}
		return false;
	}

	public int hashCode() {
		return this.statId.hashCode();
	}

}
