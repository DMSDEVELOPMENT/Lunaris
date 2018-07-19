package org.lunaris.entity.misc;

import org.lunaris.api.entity.Gamemode;
import org.lunaris.entity.LPlayer;
import org.lunaris.network.packet.Packet37AdventureSettings;

import java.util.EnumSet;
import java.util.Set;

/**
 * Created by RINES on 28.09.17.
 */
public class AdventureSettings {

    private final LPlayer player;
    private final Set<Packet37AdventureSettings.Flag> flags = EnumSet.noneOf(Packet37AdventureSettings.Flag.class);

    public AdventureSettings(LPlayer player) {
        this.player = player;
        for(Packet37AdventureSettings.Flag flag : Packet37AdventureSettings.Flag.values())
            if(flag.hasDefaultValue())
                this.flags.add(flag);
    }

    public void update() {
        Packet37AdventureSettings packet = new Packet37AdventureSettings(this.player.getEntityID(), this.flags);
        if(this.player.getGamemode() != Gamemode.CREATIVE && this.player.getGamemode() != Gamemode.SURVIVAL)
            packet
                .flag(Packet37AdventureSettings.Flag.WORLD_IMMUTABLE, true)
                .flag(Packet37AdventureSettings.Flag.WORLD_BUILDER, false)
                .flag(Packet37AdventureSettings.Flag.BUILD_AND_MINE, false);
        this.player.sendPacket(packet);
    }

    public void update(Gamemode gamemode) {
        flag(Packet37AdventureSettings.Flag.ALLOW_FLIGHT, gamemode, Gamemode.CREATIVE, Gamemode.SPECTATOR);
        flag(Packet37AdventureSettings.Flag.NO_CLIP, gamemode, Gamemode.SPECTATOR);
        flag(Packet37AdventureSettings.Flag.FLYING, gamemode, Gamemode.SPECTATOR);
        update();
    }

    private void flag(Packet37AdventureSettings.Flag flag, Gamemode given, Gamemode... allowed) {
        for(Gamemode gamemode : allowed)
            if(gamemode == given) {
                setFlag(flag, true);
                return;
            }
        setFlag(flag, false);
    }

    public void setFlag(Packet37AdventureSettings.Flag flag, boolean value) {
        if(value)
            this.flags.add(flag);
        else
            this.flags.remove(flag);
    }

    public AdventureSettings flag(Packet37AdventureSettings.Flag flag, boolean value) {
        setFlag(flag, value);
        return this;
    }

    public void setFlagAndUpdate(Packet37AdventureSettings.Flag flag, boolean value) {
        if(value && this.flags.add(flag) || !value && this.flags.remove(flag))
            update();
    }

}
