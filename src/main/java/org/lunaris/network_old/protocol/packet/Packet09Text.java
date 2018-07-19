package org.lunaris.network_old.protocol.packet;

import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;

/**
 * Created by RINES on 16.09.17.
 */
public class Packet09Text extends MinePacket {

    private MessageType type;
    private String source = "";
    private String message = "";
    private String[] parameters = new String[0];
    private String xuid = "";

    public Packet09Text() {}

    public Packet09Text(MessageType type, String source, String message, String... parameters) {
        this.type = type;
        this.source = source;
        this.message = message;
        this.parameters = parameters;
    }

    @Override
    public int getId() {
        return 0x09;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.type = MessageType.values()[buffer.readByte()];
        buffer.readBoolean(); //unspecified
        switch(type) {
            case CHAT:
            case WHISPER:
            case ANNOUNCEMENT: {
                this.source = buffer.readString();
                this.message = buffer.readString();
                this.xuid = buffer.readString();
                break;
            }case POPUP: {
                this.message = buffer.readString();
                this.source = buffer.readString();
                break;
            }case RAW:
            case TIP:
            case SYSTEM: {
                this.message = buffer.readString();
                break;
            }case TRANSLATION: {
                this.message = buffer.readString();
                int params = buffer.readUnsignedVarInt();
                this.parameters = new String[params];
                for(int i = 0; i < params; ++i)
                    this.parameters[i] = buffer.readString();
                break;
            }
        }
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeByte((byte) this.type.ordinal());
        buffer.writeBoolean(false);
        switch(this.type) {
            case CHAT:
            case WHISPER:
            case ANNOUNCEMENT: {
                buffer.writeString(this.source);
                buffer.writeString(this.message);
                buffer.writeString(this.xuid);
                break;
            }case POPUP: {
                buffer.writeString(this.message);
                buffer.writeString(this.source);
                break;
            }case RAW:
            case TIP:
            case SYSTEM: {
                buffer.writeString(this.message);
                break;
            }case TRANSLATION: {
                buffer.writeString(this.message);
                buffer.writeUnsignedVarInt(this.parameters.length);
                for(String s : this.parameters)
                    buffer.writeString(s);
                break;
            }
        }
    }

    public MessageType getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public String getMessage() {
        return message;
    }

    public String[] getParameters() {
        return parameters;
    }

    public enum MessageType {
        RAW,
        CHAT,
        TRANSLATION,
        POPUP,
        TIP,
        SYSTEM,
        WHISPER,
        ANNOUNCEMENT
    }

}
