package org.lunaris.entity.data;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ByteEntityData extends EntityData<Integer> {
    public int data;

    public ByteEntityData(int id, int data) {
        super(id);
        this.data = data;
    }

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        if (data == null) {
            this.data = 0;
        } else {
            this.data = data;
        }
    }

    @Override
    public EntityDataType getType() {
        return EntityDataType.BYTE;
    }
}
