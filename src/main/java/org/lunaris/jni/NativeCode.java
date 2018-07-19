package org.lunaris.jni;

/**
 * Created by k.shandurenko on 19.07.2018
 */
public class NativeCode<T> {

    private final String name;
    private final Class<? extends T> javaImpl;
    private final Class<? extends T> nativeImpl;

    private boolean loaded;

    public NativeCode(String name, Class<? extends T> javaImpl, Class<? extends T> nativeImpl) {
        this.name = name;
        this.javaImpl = javaImpl;
        this.nativeImpl = nativeImpl;
    }

    //TODO: lambda metafactory
    public T newInstance() {
        try {
            return this.loaded ? this.nativeImpl.newInstance() : this.javaImpl.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean load() {
        if (this.loaded || !isSupported()) {
            return this.loaded;
        }
        String fullname = "lunaris-" + this.name;
        try {
            System.loadLibrary(fullname);
            this.loaded = true;
        } catch (Throwable t) {

        }
        if (!this.loaded) {
            //TODO: some crazy code
        }
        return this.loaded;
    }

    private static boolean isSupported() {
        //return check for windows and linux x64
        return true;
    }

}
