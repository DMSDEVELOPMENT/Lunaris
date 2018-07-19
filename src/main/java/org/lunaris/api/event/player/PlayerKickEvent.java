package org.lunaris.api.event.player;

import org.lunaris.api.event.Cancellable;
import org.lunaris.api.event.Event;
import org.lunaris.entity.LPlayer;

/**
 * Created by RINES on 13.09.17.
 */
public class PlayerKickEvent extends Event implements Cancellable {

    private final LPlayer player;
    private String reason;
    private ReasonType reasonType = ReasonType.OTHER;
    private boolean cancelled;

    public PlayerKickEvent(LPlayer player) {
        this(player, null);
    }

    public PlayerKickEvent(LPlayer player, String reason) {
        this.player = player;
        this.reason = reason;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public ReasonType getReasonType() {
        return reasonType;
    }

    public void setReasonType(ReasonType reasonType) {
        this.reasonType = reasonType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LPlayer getPlayer() {
        return player;
    }

    public enum ReasonType {
        SERVER_IS_FULL,
        NOT_WHITELISTED,
        OTHER
    }

}
