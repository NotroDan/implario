package net.minecraft.resources.event.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.resources.event.events.player.PlayerEvent;

@Data
@EqualsAndHashCode (callSuper = false)
public class TrackerUpdateEvent extends PlayerEvent<TrackerUpdateEvent> {

	private final EntityTrackerEntry trackerEntry;
	private final MPlayer player;

}
