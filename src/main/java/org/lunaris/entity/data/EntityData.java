package org.lunaris.entity.data;

import java.util.Objects;

/**
 * Created by RINES on 14.09.17.
 */
public abstract class EntityData<T> {

    private int id;

    protected EntityData(int id) {
        this.id = id;
    }

    public abstract EntityDataType getType();

    public int getTypeId() {
        return getType().ordinal();
    }

    public abstract T getData();

    public abstract void setData(T data);

    public int getId() {
        return id;
    }

    public EntityData setId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EntityData && ((EntityData) obj).getId() == this.getId() && Objects.equals(((EntityData) obj).getData(), this.getData());
    }

}
