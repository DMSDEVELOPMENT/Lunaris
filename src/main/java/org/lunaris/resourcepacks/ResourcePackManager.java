package org.lunaris.resourcepacks;

/**
 * Created by RINES on 13.09.17.
 */
public class ResourcePackManager {

    private ResourcePack[] resourceStack = new ResourcePack[0];

    public boolean isResourcePackForced() {
        return false;
    }

    public ResourcePack getResourcePack(String packID) {
        return null;
    }

    public ResourcePack[] getResourceStack() {
        return this.resourceStack;
    }

}
