package org.lunaris.network.util;

import io.netty.buffer.ByteBuf;

/**
 * Created by k.shandurenko on 19.07.2018
 */
public interface ZLib {

    void init(boolean compress, int level);

    void free();

    void process(ByteBuf in, ByteBuf out);

}
