package com.jd.laf.extension;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * SPI加载插件
 */
public class SpiLoader implements ExtensionLoader {

    public static final ExtensionLoader INSTANCE = new SpiLoader();

    @Override
    public <T> Collection<Plugin<T>> load(final Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        List<Plugin<T>> result = new LinkedList<Plugin<T>>();
        ServiceLoader<T> plugins = ServiceLoader.load(clazz);
        for (T plugin : plugins) {
            result.add(new Plugin<T>(new Name(plugin.getClass()), plugin));
        }
        return result;
    }
}
