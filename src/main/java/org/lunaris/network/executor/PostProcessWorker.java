package org.lunaris.network.executor;

import io.gomint.jraknet.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.lunaris.LunarisServer;
import org.lunaris.jni.NativeCode;
import org.lunaris.jni.zlib.JavaZLib;
import org.lunaris.jni.zlib.NativeZLib;
import org.lunaris.jwt.EncryptionHandler;
import org.lunaris.network.Packet;
import org.lunaris.network.PlayerConnection;
import org.lunaris.network.PlayerConnectionState;
import org.lunaris.network.packet.PacketFEBatch;
import org.lunaris.network.util.ZLib;

import java.util.List;

/**
 * Created by k.shandurenko on 19.07.2018
 */
public class PostProcessWorker implements Runnable {
    private static final NativeCode<ZLib> ZLIB = new NativeCode<>( "zlib", JavaZLib.class, NativeZLib.class );
    private static final ThreadLocal<ZLib> COMPRESSOR = new ThreadLocal<>();

    static {
        ZLIB.load();
    }

    private final PlayerConnection connection;
    private final List<Packet> packets;
    private final Packet singlePacket;

    public PostProcessWorker(PlayerConnection connection, List<Packet> packets) {
        this.connection = connection;
        this.packets = packets;
        this.singlePacket = null;
    }

    public PostProcessWorker(PlayerConnection connection, Packet singlePacket) {
        this.connection = connection;
        this.packets = null;
        this.singlePacket = singlePacket;
    }

    private ZLib getCompressor() {
        if ( COMPRESSOR.get() == null ) {
            ZLib zLib = ZLIB.newInstance();
            zLib.init(true, LunarisServer.getInstance().getServerSettings().getNetworkCompressionLevel());
            COMPRESSOR.set( zLib );
            return zLib;
        }

        return COMPRESSOR.get();
    }

    @Override
    public void run() {
        ByteBuf inBuf = PooledByteBufAllocator.DEFAULT.directBuffer();

        // Write all packets into the inBuf for compression
        if (this.packets == null) {
            writeToBuffer(inBuf, this.singlePacket);
        } else {
            this.packets.forEach(packet -> writeToBuffer(inBuf, packet));
        }

        ZLib compressor = this.getCompressor();
        ByteBuf outBuf = PooledByteBufAllocator.DEFAULT.directBuffer( 8192 ); // We will write at least once so ensureWrite will realloc to 8192 so or so

        try {
            compressor.process( inBuf, outBuf );
        } catch ( Exception e ) {
            e.printStackTrace();
            outBuf.release();
            return;
        } finally {
            inBuf.release();
        }

        byte[] data = new byte[outBuf.readableBytes()];
        outBuf.readBytes( data );
        outBuf.release();

        PacketFEBatch batch = new PacketFEBatch();
        batch.setPayload( data );

        EncryptionHandler encryptionHandler = this.connection.getEncryptionHandler();
        if ( encryptionHandler != null && ( this.connection.getConnectionState() == PlayerConnectionState.LOGIN || this.connection.getConnectionState() == PlayerConnectionState.PLAYING ) ) {
            batch.setPayload( encryptionHandler.encryptInputForClient( batch.getPayload() ) );
        }

        this.connection.sendPacketImmediately( batch );
    }

    private void writeToBuffer(ByteBuf inBuf, Packet packet) {
        PacketBuffer buffer = new PacketBuffer(64);
        buffer.writeByte(this.singlePacket.getID());
        buffer.writeShort((short) 0);
        this.singlePacket.write(buffer);
        writeVarInt(buffer.getPosition(), inBuf);
        inBuf.writeBytes(buffer.getBuffer(), buffer.getBufferOffset(), buffer.getPosition() - buffer.getBufferOffset());
    }

    private void writeVarInt( int value, ByteBuf stream ) {
        int copyValue = value;

        while ( ( copyValue & -128 ) != 0 ) {
            stream.writeByte( copyValue & 127 | 128 );
            copyValue >>>= 7;
        }

        stream.writeByte( copyValue );
    }

}
