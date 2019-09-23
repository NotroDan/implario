package net.minecraft.resources.event.events.player;

import net.minecraft.entity.player.Player;

public abstract class APlayerСancelableEvent extends APlayerEvent implements IPlayerCancelableEvent {
    private boolean cancel;

    public APlayerСancelableEvent(Player player, boolean cancel){
        super(player);
        this.cancel = cancel;
    }

    public APlayerСancelableEvent(Player player){
        this(player, false);
    }

    @Override
    public void cancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public boolean isCanceled() {
        return cancel;
    }
}
