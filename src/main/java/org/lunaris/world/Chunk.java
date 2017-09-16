package org.lunaris.world;

import org.lunaris.block.Block;
import org.lunaris.block.Material;
import org.lunaris.entity.Player;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.packet.Packet3AFullChunkData;
import org.lunaris.util.math.Vector3d;
import org.lunaris.world.util.LongHash;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * Created by RINES on 13.09.17.
 */
public abstract class Chunk {

    private final static byte[] ETERNAL8 = new byte[1 << 8];

    private final World world;

    private final int x, z;

    private final ChunkSection[] sections = new ChunkSection[16];

    private final int[] heightmap = new int[1 << 8];

    private final int[] biomeColors = new int[1 << 8];

    private byte[] data;
    private boolean dirty = true;

    protected Chunk(World world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
        for(int i = 0; i < this.sections.length; ++i)
            this.sections[i] = new ChunkSection();
        if(!load())
            generate();
    }

    protected abstract void save();

    protected abstract boolean load();

    protected void generate() {
        if(Math.abs(this.x) < 2 && Math.abs(this.z) < 12)
            for(int x = 0; x < 16; ++x)
                for(int z = 0; z < 16; ++z)
                    setBlock(x, 32, z, x == 7 || x == 8 || z == 7 || z == 8 ? Material.STONE : Material.GRASS, 0);
    }

    void tick() {

    }

    public void sendTo(Player player) {
        player.addChunkSent(this.x, this.z);
        player.sendPacket(new Packet3AFullChunkData(this.x, this.z, compile()));
    }

    public synchronized byte[] compile() {
        if(!this.dirty)
            return this.data;
        this.dirty = false;
        recalculateHeightmap();
        MineBuffer buffer = new MineBuffer(1 << 6);
        int sectionsHeight = 0;
        for(int i = this.sections.length - 1; i >= 0; --i)
            if(!this.sections[i].isEmpty()) {
                sectionsHeight = i + 1;
                break;
            }
        buffer.writeByte((byte) sectionsHeight);
        for(int i = 0; i < sectionsHeight; ++i) {
            buffer.writeByte((byte) 0);
            buffer.writeBytes(this.sections[i].getBytes());
        }
        for(int height : this.heightmap)
            buffer.writeByte((byte) height);
        buffer.writeBytes(ETERNAL8);
        buffer.writeBytes(getBiomeIdArray());
        buffer.writeByte((byte) 0);
        if(false) { //if has extra data
            //отправить дополнительный мегабайт
        }else {
            //оставить пакет весом 15кб
            buffer.writeVarInt(0);
        }
        buffer.writeBytes(new byte[0]); //block (tile) entities
        try {
            return this.data = buffer.readBytes(buffer.readableBytes());
        }finally {
            buffer.release();
        }
    }

    public Block getBlock(Vector3d position) {
        return getBlock(position.getBlockX(), position.getBlockY(), position.getBlockZ());
    }

    public Block getBlock(int x, int y, int z) {
        ChunkSection section = getSection(y);
        return new Block(new Location(this.world, x, y, z), Material.getById(section.getId(x, y, z)), section.getData(x, y, z));
    }

    public void setBlock(Vector3d position, Material type, int data) {
        setBlock(position.getBlockX(), position.getBlockY(), position.getBlockZ(), type.getId(), data);
    }

    public void setBlock(Vector3d position, Material type) {
        setBlock(position.getBlockX(), position.getBlockY(), position.getBlockZ(), type.getId(), 0);
    }

    public void setBlock(Vector3d position, int id, int data) {
        setBlock(position.getBlockX(), position.getBlockY(), position.getBlockZ(), id, data);
    }

    public void setBlock(Vector3d position, int id) {
        setBlock(position.getBlockX(), position.getBlockY(), position.getBlockZ(), id, 0);
    }

    public void setBlock(int x, int y, int z, Material type) {
        setBlock(x, y, z, type.getId(), 0);
    }

    public void setBlock(int x, int y, int z, Material type, int data) {
        setBlock(x, y, z, type.getId(), data);
    }

    public void setBlock(int x, int y, int z, int id) {
        setBlock(x, y, z, id, 0);
    }

    public void setBlock(int x, int y, int z, int id, int data) {
        getSection(y).set(x, y, z, (short) id, (byte) data);
    }

    private void broadcastUpdate() {
        Collection<Player> players = this.world.getApplicablePlayers(this);
    }

    public World getWorld() {
        return this.world;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public int getHighestBlockAt(int x, int z) {
        return getHighestBlockAt(x, z, true);
    }

    public int getHighestBlockAt(int x, int z, boolean cached) {
        x &= 15; z &= 15;
        if(cached)
            return this.heightmap[z << 4 | x];
        byte[] column = getColumn(x, z);
        for(int y = 255; y >= 0; --y)
            if(column[y << 1] > 0 || column[(y << 1) + 1] > 0) {
                this.heightmap[z << 4 | x] = y;
                return y;
            }
        return 0;
    }

    protected ChunkSection getSection(double y) {
        return getSection((int) y);
    }

    protected ChunkSection getSection(int y) {
        return this.sections[y >> 4];
    }

    private void recalculateHeightmap() {
        for(int x = 0; x < 16; ++x)
            for(int z = 0; z < 16; ++z)
                this.heightmap[z << 4 | x] = getHighestBlockAt(x, z, false);
    }

    private byte[] getColumn(int x, int z) {
        ByteBuffer buffer = ByteBuffer.allocate(1 << 9);
        for(ChunkSection section : this.sections)
            buffer.put(section.getColumn(x, z));
        return buffer.array();
    }

    private byte[] getBiomeIdArray() {
        byte[] ids = new byte[this.biomeColors.length];
        for (int i = 0; i < this.biomeColors.length; i++) {
            int d = this.biomeColors[i];
            ids[i] = (byte) (d >> 24);
        }
        return ids;
    }

    private long hash() {
        return LongHash.toLong(this.x, this.z);
    }

}
