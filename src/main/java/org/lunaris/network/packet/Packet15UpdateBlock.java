package org.lunaris.network.packet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.gomint.jraknet.PacketBuffer;
import org.lunaris.block.LBlock;
import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;
import org.lunaris.world.BlockVector;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by RINES on 24.09.17.
 */
//TODO: отличается от мяты
public class Packet15UpdateBlock extends Packet {

    public static final int FLAG_NONE = 0b0000;
    public static final int FLAG_NEIGHBORS = 0b0001;
    public static final int FLAG_NETWORK = 0b0010;
    public static final int FLAG_NOGRAPHIC = 0b0100;
    public static final int FLAG_PRIORITY = 0b1000;
    public static final int FLAG_ALL = FLAG_NEIGHBORS | FLAG_NETWORK;
    public static final int FLAG_ALL_PRIORITY = FLAG_ALL | FLAG_PRIORITY;

    private static int RUNTIME_BLOCK_IDS[][];

    private int x, y, z;
    private int id, flag, data;

    public Packet15UpdateBlock() {}

    public Packet15UpdateBlock(LBlock block) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.id = block.getTypeId();
        this.data = block.getData();
    }

    public Packet15UpdateBlock(int x, int y, int z, int id, int data) {
        this(x, y, z, id, FLAG_ALL_PRIORITY, data);
    }

    public Packet15UpdateBlock(int x, int y, int z, int id, int flag, int data) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.flag = flag;
        this.data = data;
    }

    @Override
    public byte getID() {
        return 0x15;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        SerializationUtil.writeBlockVector(new BlockVector(this.x, this.y, this.z), buffer);
        buffer.writeUnsignedVarInt(getBlockRuntimeID(this.id, this.data));
        buffer.writeUnsignedVarInt(this.flag);
        buffer.writeUnsignedVarInt(0); //layer
    }

    static {
        try (InputStream inputStream = Packet15UpdateBlock.class.getResourceAsStream( "/block_ids.json" );
             InputStreamReader reader = new InputStreamReader(inputStream)) {
            JsonParser parser = new JsonParser();
            JsonArray blocks = parser.parse(reader).getAsJsonArray();
            Map<Integer, Integer> maxIds = new HashMap<>();
            for (JsonElement block : blocks) {
                JsonObject casted = block.getAsJsonObject();
                int id = casted.get("id").getAsInt();
                int data = casted.get("data").getAsInt();
                Integer maxData = maxIds.get(id);
                if (maxData == null || maxData < data) {
                    maxIds.put(id, data);
                }
            }
            RUNTIME_BLOCK_IDS = new int[maxIds.keySet().stream().mapToInt(i -> i).max().getAsInt() + 1][];
            maxIds.forEach((id, data) -> RUNTIME_BLOCK_IDS[id] = new int[data + 1]);
            for (JsonElement block : blocks) {
                JsonObject casted = block.getAsJsonObject();
                int id = casted.get("id").getAsInt();
                int data = casted.get("data").getAsInt();
                RUNTIME_BLOCK_IDS[id][data] = casted.get("runtimeID").getAsInt();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static int getBlockRuntimeID(int blockID, int blockData) {
        if (blockID >= RUNTIME_BLOCK_IDS.length) {
            return 0;
        }
        if (blockData >= RUNTIME_BLOCK_IDS[blockID].length) {
            return 0;
        }
        return RUNTIME_BLOCK_IDS[blockID][blockData];
    }

}
