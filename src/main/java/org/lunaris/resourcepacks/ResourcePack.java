package org.lunaris.resourcepacks;

/**
 * Created by RINES on 13.09.17.
 */
public interface ResourcePack {
    String getPackName();

    String getPackId();

    String getPackVersion();

    int getPackSize();

    byte[] getSha256();

    byte[] getPackChunk(int off, int len);
}