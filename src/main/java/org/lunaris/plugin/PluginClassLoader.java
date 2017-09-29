package org.lunaris.plugin;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author xtrafrancyz
 */
class PluginClassLoader extends URLClassLoader {
    private final PluginManager pluginManager;

    PluginClassLoader(URL[] urls, ClassLoader parent, PluginManager pluginManager) {
        super(urls, parent);
        this.pluginManager = pluginManager;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass0(name, true);
    }

    Class<?> findClass0(String name, boolean findGlobal) throws ClassNotFoundException {
        if (!name.startsWith("org.lunaris.")) {
            Class<?> result = null;
            if (findGlobal)
                result = pluginManager.findClassInPlugins(name);
            if (result == null)
                result = super.findClass(name);
            if (result != null)
                pluginManager.cachedClasses.put(name, result);
            return result;
        } else {
            throw new ClassNotFoundException(name);
        }
    }
}
