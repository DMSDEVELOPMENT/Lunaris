package org.lunaris.entity;

import org.lunaris.entity.data.Attribute;

/**
 * Created by RINES on 14.09.17.
 */
public class LivingEntity extends Entity {

    protected LivingEntity(long entityID) {
        super(entityID);
    }

    public float getHealth() {
        return getAttribute(Attribute.MAX_HEALTH).getValue();
    }

    public float getMaxHealth() {
        return getAttribute(Attribute.MAX_HEALTH).getMaxValue();
    }

    public void setHealth(float value) {
        setAttribute(Attribute.MAX_HEALTH, value);
    }

    public void setMaxHealth(float value) {
        Attribute a = getAttribute(Attribute.MAX_HEALTH);
        a.setMaxValue(value);
        setHealth(a.getValue());
    }

}
