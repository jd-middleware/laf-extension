package com.jd.laf.extension;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import static java.util.Collections.sort;

/**
 * SPI加载插件
 */
public class SpiLoader implements ExtensionLoader {

    public static final ExtensionLoader INSTANCE = new SpiLoader();

    @Override
    public <T, M> ExtensionSpi<T, M> load(final Class<T> clazz,
                                          final Comparator<ExtensionMeta<T, M>> comparator,
                                          final Classify<T, M> classify) {
        if (clazz == null) {
            return null;
        }

        Extensible extensible = clazz.getAnnotation(Extensible.class);
        Name extensibleName = new Name(clazz, extensible != null && extensible.value() != null
                && !extensible.value().isEmpty() ? extensible.value() : clazz.getName());
        List<ExtensionMeta<T, M>> metas = new LinkedList<ExtensionMeta<T, M>>();
        //从SPI加载插件
        if (loadExtensions(clazz, extensibleName, metas, classify)) {
            //排序
            sort(metas, comparator == null ? AscendingComparator.INSTANCE : comparator);
        }

        return new ExtensionSpi(extensibleName, metas);
    }

    /**
     * 加载插件
     *
     * @param clazz
     * @return 是否要排序
     */
    protected <T, M> boolean loadExtensions(final Class<T> clazz,
                                            final Name extensibleName,
                                            final List<ExtensionMeta<T, M>> metas,
                                            final Classify<T, M> classify) {
        ServiceLoader<T> plugins = ServiceLoader.load(clazz);
        int last = Ordered.ORDER;
        int count = 0;
        boolean result = false;
        for (T plugin : plugins) {
            ExtensionMeta<T, M> meta = build(plugin, extensibleName, classify, Instance.ClazzInstance.INSTANCE);
            metas.add(meta);
            if (count++ > 0 && meta.getOrder() != last) {
                //顺序不一样，需要排序
                result = true;
            }
            last = meta.getOrder();
        }
        return result;
    }

    /**
     * 构建元数据
     *
     * @param plugin
     * @param extensibleName
     * @param classify
     * @param instance
     * @param <T>
     * @param <M>
     * @return
     */
    protected <T, M> ExtensionMeta<T, M> build(final T plugin, final Name extensibleName, final Classify<T, M> classify, final Instance instance) {
        Class<?> serviceClass = plugin.getClass();
        Name name = new Name(serviceClass);
        Extension extension = serviceClass.getAnnotation(Extension.class);
        ExtensionMeta<T, M> meta = new ExtensionMeta<T, M>();
        meta.setExtensible(extensibleName);
        meta.setTarget(plugin);

        M pluginName = null ;
        if(classify != null) {
            pluginName = classify.type(plugin) ;
        } else if (Type.class.isAssignableFrom(serviceClass)) {
            pluginName = (M)((Type) plugin).type() ;
        } else if(extension != null) {
            if(extension.value() != null && !extension.value().isEmpty()) {
                pluginName = (M)extension.value() ;
            } else {
                pluginName = (M)serviceClass.getName() ;
            }
        }
        meta.setExtension(new Name(serviceClass , pluginName));

        meta.setSingleton(Prototype.class.isAssignableFrom(serviceClass) ? false : (extension == null ? true : extension.singleton()));
        meta.setOrder(Ordered.class.isAssignableFrom(serviceClass) ?
                ((Ordered) plugin).order() : (extension == null ? Ordered.ORDER : extension.order()));
        meta.setInstance(instance);
        meta.setName(name);
        return meta;
    }

}
