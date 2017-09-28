package org.lunaris.entity.data;

import org.lunaris.entity.Player;
import org.lunaris.network.protocol.packet.Packet37AdventureSettings;

import java.util.EnumSet;
import java.util.Set;

/**
 * Created by RINES on 28.09.17.
 */
public class AdventureSettings {

    private final Player player;
    private final Set<Packet37AdventureSettings.Flag> flags = EnumSet.noneOf(Packet37AdventureSettings.Flag.class);

    public AdventureSettings(Player player) {
        this.player = player;
        for(Packet37AdventureSettings.Flag flag : Packet37AdventureSettings.Flag.values())
            if(flag.hasDefaultValue())
                this.flags.add(flag);
    }

    public void update() {
        this.player.sendPacket(new Packet37AdventureSettings(this.flags));
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
