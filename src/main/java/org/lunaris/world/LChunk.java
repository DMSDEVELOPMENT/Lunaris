package org.lunaris.world;

import org.lunaris.LunarisServer;
import org.lunaris.api.entity.Player;
import org.lunaris.api.world.Chunk;
import org.lunaris.api.world.Location;
import org.lunaris.block.LBlock;
import org.lunaris.entity.LPlayer;
import org.lunaris.api.material.Material;
import org.lunaris.network.Packet;
import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;
import org.lunaris.network.packet.Packet3AFullChunkData;
import org.lunaris.api.util.math.Vector3d;
import org.lunaris.world.util.LongHash;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * Created by RINES on 13.09.17.
 */
public abstract class LChunk implements Chunk {
    private static final int WORLD_LIMIT = 3_000_000;

    private final static byte[] ETERNAL8 = new byte[1 << 8];

    private final LWorld world;

    protected final int x, z;

    private final ChunkSection[] sections = new ChunkSection[16];

    private final int[] heightmap = new int[1 << 8];

    private final int[] biomeColors = new int[1 << 8];

    private byte[] data;
    private boolean dirty = true;
    private boolean loaded = false;

    protected LChunk(LWorld world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
        for (int i = 0; i < this.sections.length; ++i)
            this.sections[i] = new ChunkSection();
    }

    void save() {
        save0();
    }

    protected abstract void save0();

    void load() {
        load0();
        loaded = true;
        //System.out.println("Chunk " + getX() + " " + getZ() + " loaded");
        LunarisServer.getInstance().getScheduler().run(() -> {
            for (LPlayer player : getWatcherPlayers())
                sendTo(player);
        });
    }

    protected abstract void load0();

    void tick() {

    }

    public void sendTo(LPlayer player) {
        if (!loaded)
            return;
        player.addChunkSent(this.x, this.z);
        player.sendPacket(new Packet3AFullChunkData(this.x, this.z, compile()));
    }

    public synchronized byte[] compile() {
        if (!this.dirty)
            return this.data;
        this.dirty = false;
        recalculateHeightmap();
        MineBuffer buffer = new MineBuffer(1 << 6);
        int sectionsHeight = 0;
        for (int i = this.sections.length - 1; i >= 0; --i)
            if (!this.sections[i].isEmpty()) {
                sectionsHeight = i + 1;
                break;
            }
        buffer.writeByte((byte) sectionsHeight);
        for (int i = 0; i < sectionsHeight; ++i) {
            buffer.writeByte((byte) 0);
            buffer.writeBytes(this.sections[i].getBytes());
        }
        for (int height : this.heightmap)
            buffer.writeByte((byte) height);
        buffer.writeBytes(ETERNAL8);
        buffer.writeBytes(getBiomeIdArray());
        buffer.writeByte((byte) 0);
        if (false) { //if has extra data
            //отправить дополнительный мегабайт
        } else {
            //оставить пакет весом 15кб
            buffer.writeVarInt(0);
        }
        buffer.writeBytes(new byte[0]); //block (tile) entities
        try {
            return this.data = buffer.readBytes(buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }

    public LBlock getBlock(Vector3d position) {
        return getBlock(position.getBlockX(), position.getBlockY(), position.getBlockZ());
    }

    public LBlock getBlock(int x, int y, int z) {
        if (x >= WORLD_LIMIT || x < -WORLD_LIMIT || y < 0 || y > 255 || z >= WORLD_LIMIT || z < -WORLD_LIMIT)
            return new LBlock(new Location(this.world, x, y, z), Material.AIR);
        ChunkSection section = getSection(y);
        return new LBlock(new Location(this.world, x, y, z), Material.getById(section.getId(x, y, z)), section.getData(x, y, z));
    }

    protected void setBlock(int x, int y, int z, Material type) {
        setBlock0(x, y, z, type.getId(), 0);
    }

    protected void setBlock(int x, int y, int z, Material type, int data) {
        setBlock0(x, y, z, type.getId(), data);
    }

    protected void setBlock(int x, int y, int z, int id) {
        setBlock0(x, y, z, id, 0);
    }

    protected void setBlock(int x, int y, int z, int id, int data) {
        setBlock0(x, y, z, id, data);
    }

    void setBlock0(int x, int y, int z, int id, int data) {
        if (x >= WORLD_LIMIT || x < -WORLD_LIMIT || y < 0 || y > 255 || z >= WORLD_LIMIT || z < -WORLD_LIMIT)
            return;
        getSection(y).set(x, y, z, (short) id, (byte) data);
        this.dirty = true;
    }

    /**
     * Отсылает пакет всем игрокам, которые видят этот чанк (но могу ти не находиться в нем).
     */
    public void sendPacket(Packet packet) {
        LunarisServer.getInstance().getNetworkManager().sendPacket(getWatcherPlayers(), packet);
    }

    public LWorld getWorld() {
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
        x &= 15;
        z &= 15;
        if (cached)
            return this.heightmap[z << 4 | x];
        byte[] column = getColumn(x, z);
        for (int y = 255; y >= 0; --y)
            if (column[y << 1] > 0 || column[(y << 1) + 1] > 0) {
                this.heightmap[z << 4 | x] = y;
                return y;
            }
        return 0;
    }

    /**
     * Возвращает коллекцию игроков, которые видят этот чанк (но могут и не находиться в нем).
     */
    public Collection<LPlayer> getWatcherPlayers() {
        return this.world.getApplicablePlayers(this);
    }

    protected ChunkSection getSection(double y) {
        return getSection((int) y);
    }

    protected ChunkSection getSection(int y) {
        return this.sections[y >> 4];
    }

    private void recalculateHeightmap() {
        for (int x = 0; x < 16; ++x)
            for (int z = 0; z < 16; ++z)
                this.heightmap[z << 4 | x] = getHighestBlockAt(x, z, false);
    }

    private byte[] getColumn(int x, int z) {
        ByteBuffer buffer = ByteBuffer.allocate(1 << 9);
        for (ChunkSection section : this.sections)
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

    public boolean isLoaded() {
        return this.loaded;
    }

    @Override
    public void unload() {
        this.world.unloadChunk(this);
    }

    @Override
    public boolean isInRangeOfViewFor(Player player) {
        return this.world.isInRangeOfView(player, this);
    }

}
