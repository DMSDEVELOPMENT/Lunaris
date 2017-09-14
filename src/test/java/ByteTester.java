import java.util.Arrays;

/**
 * Created by RINES on 13.09.17.
 */
public class ByteTester {

    public static void main(String[] args) {
        ChunkSection section = new ChunkSection();
        section.set(111, 128, 139, (short) 7, (byte) 0);
        System.out.println(section.i(111, 128, 139) + " " + section.getId(111, 128, 139));
    }

    public static class ChunkSection {

        private final byte[] ids;
        private final byte[] datas;
        private boolean empty;

        ChunkSection() {
            this(new byte[1 << 13], new byte[1 << 12], true);
            Arrays.fill(this.datas, (byte) -1);
        }

        ChunkSection(byte[] ids, byte[] datas, boolean empty) {
            this.ids = ids;
            this.datas = datas;
            this.empty = empty;
        }

        short getId(int x, int y, int z) {
            int key = i(x, y, z);
            byte right = this.ids[key << 1];
            byte left = this.ids[(key << 1) + 1];
            return (short) ((right & 0xff) + ((byte) (left & 0xFF) << 8));
        }

        byte getData(int x, int y, int z) {
            return this.datas[i(x, y, z)];
        }

        void set(int x, int y, int z, short id, byte data) {
            if(id > 0)
                this.empty = false;
            int i = i(x, y, z);
            this.ids[i << 1] = (byte) (id & 0xff);
            this.ids[(i << 1) + 1] = (byte) ((id >>> 8) & 0xff);
            this.datas[i] = data;
        }

        boolean isEmpty() {
            return this.empty;
        }

        private int i(int x, int y, int z) {
            x &= 0xf; y &= 0xf; z &= 0xf;
            return y << 8 | z << 4 | x;
        }

    }

}
