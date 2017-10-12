package org.lunaris.network.protocol.packet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.lunaris.LunarisServer;
import org.lunaris.entity.misc.Skin;
import org.lunaris.jwt.JwtAlgorithm;
import org.lunaris.jwt.JwtSignatureException;
import org.lunaris.jwt.JwtToken;
import org.lunaris.jwt.MojangChainValidator;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;
import java.util.UUID;

/**
 * Created by RINES on 13.09.17.
 */
public class Packet01Login extends MinePacket {

    private static Boolean validate;

    private String username;
    private UUID clientUuid;
    private String xboxID;
    private ECPublicKey clientPublicKey;

    private String disconnectReason;

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
        decode(buffer.readByteArray());
    }

    private void decode(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        byte[] stringBuffer = new byte[buffer.getInt()];
        buffer.get(stringBuffer);
        String jwt = new String( stringBuffer );
        JSONObject json;
        try {
            json = this.parseJwtString( jwt );
        } catch ( ParseException e ) {
            e.printStackTrace();
            return;
        }

        Object jsonChainRaw = json.get( "chain" );
        if ( jsonChainRaw == null || !( jsonChainRaw instanceof JSONArray) ) {
            return;
        }

        MojangChainValidator chainValidator = new MojangChainValidator(LunarisServer.getInstance().getEncryptionKeyFactory());
        JSONArray jsonChain = (JSONArray) jsonChainRaw;
        for ( Object jsonTokenRaw : jsonChain ) {
            if ( jsonTokenRaw instanceof String ) {
                try {
                    JwtToken token = JwtToken.parse( (String) jsonTokenRaw );
                    chainValidator.addToken( token );
                } catch ( IllegalArgumentException e ) {
                    e.printStackTrace();
                }
            }
        }

        if(validate == null)
            validate = LunarisServer.getInstance().getServerSettings().isInOnlineMode();
        if (validate && !chainValidator.validate()) {
            this.disconnectReason = "You can login only using XBOX account.";
            return;
        }
        byte[] skin = new byte[buffer.getInt()];
        buffer.get( skin );

        JwtToken skinToken = JwtToken.parse( new String( skin ) );

        try {
            skinToken.validateSignature( JwtAlgorithm.ES384, chainValidator.getTrustedKeys().get( skinToken.getHeader().getProperty( "x5u" ) ) );
        } catch ( JwtSignatureException e ) {
            if(validate) {
                this.disconnectReason = "Your skin is invalid or corrupted.";
                return;
            }
        }
        String capeData = skinToken.getClaim( "CapeData" );
        this.skin = new Skin((String) skinToken.getClaim("SkinData"), skinToken.getClaim("SkinId"));
        if(!capeData.isEmpty())
            this.skin.setCape(this.skin.new Cape(Base64.getDecoder().decode(capeData)));
        this.skinGeometryName = skinToken.getClaim("SkinGeometryName");
        this.skinGeometry = Base64.getDecoder().decode((String) skinToken.getClaim("SkinGeometry"));

        this.username = chainValidator.getUsername();
        this.clientUuid = chainValidator.getUuid();
        this.xboxID = chainValidator.getXboxId();
        this.clientPublicKey = chainValidator.getClientPublicKey();
    }

    private JSONObject parseJwtString( String jwt ) throws ParseException {
        Object jsonParsed = new JSONParser().parse( jwt );
        if ( jsonParsed instanceof JSONObject ) {
            return (JSONObject) jsonParsed;
        } else {
            throw new ParseException( ParseException.ERROR_UNEXPECTED_TOKEN );
        }
    }

    @Override
    public void write(MineBuffer buffer) {
        throw new IllegalStateException();
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    public String getUsername() {
        return this.username;
    }

    public UUID getClientUuid() {
        return this.clientUuid;
    }

    public String getXboxID() {
        return this.xboxID;
    }

    public Skin getSkin() {
        return this.skin;
    }

    public String getSkinGeometryName() {
        return this.skinGeometryName;
    }

    public byte[] getSkinGeometry() {
        return this.skinGeometry;
    }

    public ECPublicKey getClientPublicKey() {
        return this.clientPublicKey;
    }

    public String getDisconnectReason() {
        return this.disconnectReason;
    }

}
