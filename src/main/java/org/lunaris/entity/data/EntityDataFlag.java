package org.lunaris.entity.data;

/**
 * Created by RINES on 14.09.17.
 */
public enum EntityDataFlag {
    PLAYER_SLEEP( 1 ),

    ONFIRE( 0 ),
    SNEAKING( 1 ),
    RIDING( 2 ),
    SPRINTING( 3 ),
    ACTION( 4 ),
    INVISIBLE( 5 ),
    IGNITED( 10 ),
    CAN_SHOW_NAMETAG( 14 ),
    ALWAYS_SHOW_NAMETAG( 15 ),
    IMMOBILE( 16 ),
    CAN_CLIMB( 19 ),
    SWIMMER( 20 ),
    CAN_FLY( 21 ),
    GLIDING( 31 ),
    BREATHING( 34 ),

    HAS_COLLISION( 46 ),
    AFFECTED_BY_GRAVITY( 47 ),

    SWIMMING( 55 );

    private final int id;

    EntityDataFlag(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

}
