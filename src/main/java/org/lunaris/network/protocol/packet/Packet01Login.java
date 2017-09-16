package org.lunaris.network.protocol.packet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.lunaris.entity.data.Skin;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by RINES on 13.09.17.
 */
public class Packet01Login extends MinePacket {

    private String username;
    private UUID clientUuid;
    private long clientId;

    private Skin skin;
    private String skinGeometryName;
    private byte[] skinGeometry;

    private int protocolVersion;

    @Override
    public int getId() {
        return 0x01;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.protocolVersion = buffer.readInt();
        buffer.readUnsignedVarInt(); //byte array (payload) size
        decode(buffer);
    }

    private void decode(MineBuffer buffer) {
        try {
            Map<String, List<String>> map = new Gson().fromJson(new String(buffer.readBytes(buffer.readUnsignedInt()), StandardCharsets.UTF_8),
                    new TypeToken<Map<String, List<String>>>() {
                    }.getType());
            if (map.isEmpty() || !map.containsKey("chain") || map.get("chain").isEmpty()) return;
            List<String> chains = map.get("chain");
            for (String c : chains) {
                JsonObject chainMap = decodeToken(c);
                if (chainMap == null) continue;
                if (chainMap.has("extraData")) {
                    JsonObject extra = chainMap.get("extraData").getAsJsonObject();
                    if (extra.has("displayName")) this.username = extra.get("displayName").getAsString();
                    if (extra.has("identity")) this.clientUuid = UUID.fromString(extra.get("identity").getAsString());
                }
            }
        }finally {
            decodeSkinData(buffer);
        }
    }

    private void decodeSkinData(MineBuffer buffer) {
        JsonObject skinToken = decodeToken(new String(buffer.readBytes(buffer.readUnsignedInt())));
        String skinId = null;
        if (skinToken.has("ClientRandomId")) this.clientId = skinToken.get("ClientRandomId").getAsLong();
        if (skinToken.has("SkinId")) skinId = skinToken.get("SkinId").getAsString();
        if (skinToken.has("SkinData")) this.skin = new Skin(skinToken.get("SkinData").getAsString(), skinId);
        if (skinToken.has("SkinGeometryName")) this.skinGeometryName = skinToken.get("SkinGeometryName").getAsString();
        if (skinToken.has("SkinGeometry")) this.skinGeometry = Base64.getDecoder().decode(skinToken.get("SkinGeometry").getAsString());
    }

    private JsonObject decodeToken(String token) {
        String[] base = token.split("\\.");
        if (base.length < 2) return null;
        return new Gson().fromJson(new String(Base64.getDecoder().decode(base[1]), StandardCharsets.UTF_8), JsonObject.class);
    }

    @Override
    public void write(MineBuffer buffer) {
        throw new IllegalStateException();
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public String getUsername() {
        return username;
    }

    public UUID getClientUuid() {
        return clientUuid;
    }

    public long getClientId() {
        return clientId;
    }

    public Skin getSkin() {
        return skin;
    }

    public String getSkinGeometryName() {
        return skinGeometryName;
    }

    public byte[] getSkinGeometry() {
        return skinGeometry;
    }
}
