package org.lunaris.network.util;

import org.lunaris.Lunaris;
import org.lunaris.jwt.EncryptionHandler;
import org.lunaris.network.raknet.session.RakNetClientSession;

/**
 * Created by RINES on 30.09.17.
 */
public class PacketsBush {

    private final static byte prefix = (byte) 0xfe;
    private final static int compressionLevel = Lunaris.getInstance().getServerSettings().getNetworkCompressionLevel();

    private final RakNetClientSession session;
    private EncryptionHandler encryptor;

    private byte[] data = new byte[16];
    private int actualLength = 0;
    private int maxLength;

    public PacketsBush(RakNetClientSession session) {
        this.session = session;
        this.maxLength = (int) (session.getMaximumTransferUnit() * 1.3f);
    }

    public void setupEncryptor(EncryptionHandler encryptor) {
        this.encryptor = encryptor;
    }

    public EncryptionHandler getEncryptor() {
        return this.encryptor;
    }

    public void collect(byte[] data) {
        if(this.actualLength + data.length > this.data.length) {
            int newLength = this.data.length << 1;
            while(this.actualLength + data.length > newLength)
                newLength <<= 1;
            byte[] temporary = this.data;
            this.data = new byte[newLength];
            System.arraycopy(temporary, 0, this.data, 0, this.actualLength);
        }
        System.arraycopy(data, 0, this.data, this.actualLength, data.length);
        this.actualLength += data.length;
    }
    
    public boolean isFull() {
        return this.actualLength >= maxLength;
    }

    public byte[] blossom() {
        try {
            if(this.data.length > 0) {
                byte[] actual = new byte[this.actualLength];
                System.arraycopy(this.data, 0, actual, 0, this.actualLength);
                this.actualLength = 0;
                actual = ZLib.deflate(actual, compressionLevel);
                if(this.session.getConnectionState() == ConnectionState.LOGGED_IN && this.encryptor != null)
                    actual = this.encryptor.encryptInputForClient(actual);
                byte[] result = new byte[actual.length + 1];
                result[0] = prefix;
                System.arraycopy(actual, 0, result, 1, actual.length);
                if (this.data.length > 100_000)
                    this.data = new byte[16];
                return result;
            }
            return this.data;
        }catch(Exception ex) {
            ex.printStackTrace();
            return new byte[0];
        }
    }

}
