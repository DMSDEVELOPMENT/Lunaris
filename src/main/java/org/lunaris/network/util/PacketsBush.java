package org.lunaris.network.util;

import org.lunaris.Lunaris;

/**
 * Created by RINES on 30.09.17.
 */
public class PacketsBush {

    private final static byte prefix = Lunaris.getInstance().getServerSettings().getNetworkPacketPrefixedId();
    private final static int compressionLevel = Lunaris.getInstance().getServerSettings().getNetworkCompressionLevel();

    private byte[] data = new byte[0];

    public synchronized void collect(byte[] data) {
        byte[] temporary = this.data;
        this.data = new byte[temporary.length + data.length];
        System.arraycopy(temporary, 0, this.data, 0, temporary.length);
        System.arraycopy(data, 0, this.data, temporary.length, data.length);
    }

    public synchronized byte[] blossom() {
        try {
            if(this.data.length > 0) {
                this.data = ZLib.deflate(this.data, compressionLevel);
                byte[] result = new byte[this.data.length + 1];
                result[0] = prefix;
                System.arraycopy(this.data, 0, result, 1, this.data.length);
                this.data = new byte[0];
                return result;
            }
            return this.data;
        }catch(Exception ex) {
            ex.printStackTrace();
            return new byte[0];
        }
    }

}
