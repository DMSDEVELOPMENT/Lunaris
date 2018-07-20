package org.lunaris.jni.zlib;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import org.lunaris.network.util.ZLib;

/**
 * Created by k.shandurenko on 19.07.2018
 */
public class NativeZLib implements ZLib {
    private final NativeZLibImpl nativeCompress = new NativeZLibImpl();

    /*============================================================================*/
    private boolean compress;
    private long ctx;

    @Override
    public void init(boolean compress, int level) {
        free();

        this.compress = compress;
        this.ctx = nativeCompress.init(compress, level);
    }

    @Override
    public void free() {
        if (ctx != 0) {
            nativeCompress.end(ctx, compress);
            ctx = 0;
        }

        nativeCompress.consumed = 0;
        nativeCompress.finished = false;
    }

    @Override
    public void process(ByteBuf in, ByteBuf out) {
        // Smoke tests
        in.memoryAddress();
        out.memoryAddress();
        Preconditions.checkState(ctx != 0, "Invalid pointer to compress!");

        try {
            while (!nativeCompress.finished && (compress || in.isReadable())) {
                out.ensureWritable(8192);

                int processed = nativeCompress.process(ctx, in.memoryAddress() + in.readerIndex(), in.readableBytes(), out.memoryAddress() + out.writerIndex(), out.writableBytes(), compress);

                in.readerIndex(in.readerIndex() + nativeCompress.consumed);
                out.writerIndex(out.writerIndex() + processed);
            }
        } finally {
            nativeCompress.reset(ctx, compress);
            nativeCompress.consumed = 0;
            nativeCompress.finished = false;
        }
    }
}
