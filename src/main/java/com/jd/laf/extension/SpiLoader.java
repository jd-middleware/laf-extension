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
        int order;
        int last = Ordered.ORDER;
        int count = 0;
        boolean result = false;
        for (T plugin : plugins) {
            Class<?> serviceClass = plugin.getClass();
            Name name = new Name(serviceClass);
            Extension extension = serviceClass.getAnnotation(Extension.class);
            //执行顺序
            order = Ordered.class.isAssignableFrom(serviceClass) ?
                    ((Ordered) plugin).order() : (extension == null ? Ordered.ORDER : extension.order());
            ExtensionMeta meta = new ExtensionMeta();
            meta.setExtensible(extensibleName);
            meta.setTarget(plugin);
            meta.setExtension(new Name(serviceClass,
                    classify != null ? classify.type(plugin) :
                            (Type.class.isAssignableFrom(serviceClass) ? ((Type) plugin).type() :
                                    (extension != null && extension.value() != null && !extension.value().isEmpty() ? extension.value() :
                                            serviceClass.getName()))));
            meta.setSingleton(extension == null ? true : extension.singleton());
            meta.setOrder(order);
            meta.setInstance(Instance.ClazzInstance.INSTANCE);
            meta.setName(name);
            metas.add(meta);
            if (count++ > 0 && order != last) {
                //顺序不一样，需要排序
                result = true;
            }
            last = order;
        }
        return result;
    }

}
