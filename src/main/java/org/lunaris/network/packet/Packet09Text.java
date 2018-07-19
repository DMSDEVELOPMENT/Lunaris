package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 16.09.17.
 */
public class Packet09Text extends Packet {

    private MessageType type;
    private String sender = "";
    private String message = "";
    private String[] parameters = new String[0];
    private String xuid = "";

    private String sourceThirdPartyName = "";
    private int sourcePlatform = 0;

    public Packet09Text() {}

    public Packet09Text(MessageType type, String sender, String message, String... parameters) {
        this.type = type;
        this.sender = sender;
        this.message = message;
        this.parameters = parameters;
    }

    @Override
    public byte getID() {
        return 0x09;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.type = MessageType.values()[buffer.readByte()];
        buffer.readBoolean(); //unspecified
        switch(type) {
            case POPUP: {
                this.message = buffer.readString();
                this.sender = buffer.readString();
                break;
            }
            case CHAT:
            case WHISPER:
            case ANNOUNCEMENT: {
                this.sender = buffer.readString();
                this.sourceThirdPartyName = buffer.readString();
                this.sourcePlatform = buffer.readSignedVarInt();
            }
            case CLIENT_MESSAGE:
            case TIP:
            case SYSTEM: {
                this.message = buffer.readString();
                break;
            }
            case JUKEBOX_POPUP:
            case TRANSLATION: {
                this.message = buffer.readString();
                byte count = buffer.readByte();
                this.parameters = new String[count];
                for (byte i = 0; i < count; ++i) {
                    this.parameters[i] = buffer.readString();
                }
                break;
            }
            default: {
                break;
            }
        }
        this.xuid = buffer.readString();
        buffer.readString();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeByte((byte) this.type.ordinal());
        buffer.writeBoolean(false);
        switch(this.type) {
            case CHAT:
            case WHISPER:
            case ANNOUNCEMENT: {
                buffer.writeString(this.sender);
                buffer.writeString(this.sourceThirdPartyName);
                buffer.writeSignedVarInt(this.sourcePlatform);
            }
            case CLIENT_MESSAGE:
            case TIP:
            case SYSTEM: {
                buffer.writeString(this.message);
                break;
            }
            case POPUP:
            case JUKEBOX_POPUP:
            case TRANSLATION: {
                buffer.writeString(this.message);
                buffer.writeByte((byte) this.parameters.length);
                for (String param : this.parameters) {
                    buffer.writeString(param);
                }
                break;
            }
            default: {
                break;
            }
        }
        buffer.writeString(this.xuid);
        buffer.writeString(""); //???
    }

    public MessageType getType() {
        return this.type;
    }

    public String getSender() {
        return this.sender;
    }

    public String getMessage() {
        return this.message;
    }

    public String[] getParameters() {
        return this.parameters;
    }

    public String getXuid() {
        return this.xuid;
    }

    public String getSourceThirdPartyName() {
        return this.sourceThirdPartyName;
    }

    public int getSourcePlatform() {
        return this.sourcePlatform;
    }

    public enum MessageType {
        CLIENT_MESSAGE, //RAW
        CHAT,
        TRANSLATION,
        POPUP,
        JUKEBOX_POPUP,
        TIP,
        SYSTEM,
        WHISPER,
        ANNOUNCEMENT
    }

}
