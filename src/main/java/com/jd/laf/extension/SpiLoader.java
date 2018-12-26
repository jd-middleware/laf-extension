package com.jd.laf.extension;

import java.util.*;

/**
 * SPI加载插件
 */
public class SpiLoader implements ExtensionLoader {

    public static final ExtensionLoader INSTANCE = new SpiLoader();

    @Override
    public ExtensionSpi load(final Class<?> clazz, final Comparator<ExtensionMeta> comparator) {
        if (clazz == null) {
            return null;
        }

        Extensible extensible = clazz.getAnnotation(Extensible.class);
        Name extensibleName = new Name(clazz, extensible != null && extensible.value() != null
                && !extensible.value().isEmpty() ? extensible.value() : clazz.getName());
        List<ExtensionMeta> metas = new ArrayList<ExtensionMeta>();
        Iterable<?> targets = loadExtensions(clazz);
        Class<?> serviceClass;
        Object target;
        Instance instance;
        //实例名称，可以保存Spring的Bean的名称
        Name name;
        //遍历扩展点
        for (Object service : targets) {
            //如果扩展点是实例描述信息，例如Spring的加载器
            if (service instanceof Instantiation) {
                Instantiation instantiation = (Instantiation) service;
                name = instantiation.getName();
                serviceClass = name.getClazz();
                target = instantiation.getTarget();
                instance = instantiation.getInstance();
            } else {
                serviceClass = service.getClass();
                target = service;
                instance = Instance.ClazzInstance.INSTANCE;
                name = new Name(serviceClass);
            }
            Extension extension = serviceClass.getAnnotation(Extension.class);
            ExtensionMeta meta = new ExtensionMeta();
            meta.setExtensible(extensibleName);
            meta.setTarget(target);
            meta.setExtension(new Name(serviceClass, Type.class.isAssignableFrom(serviceClass) ?
                    ((Type) target).type() :
                    (extension != null && extension.value() != null && !extension.value().isEmpty() ?
                            extension.value() : serviceClass.getName())));
            meta.setSingleton(extension == null ? true : extension.singleton());
            meta.setOrder(Ordered.class.isAssignableFrom(serviceClass) ?
                    ((Ordered) target).order() : (extension == null ? Ordered.ORDER : extension.order()));
            meta.setInstance(instance != null ? instance : Instance.ClazzInstance.INSTANCE);
            meta.setName(name);
            metas.add(meta);
        }

        //排序
        Collections.sort(metas, comparator == null ? AscendingComparator.INSTANCE : comparator);

        return new ExtensionSpi(extensibleName, metas);
    }

    /**
     * 加载插件
     *
     * @param clazz
     * @return
     */
    protected Iterable<?> loadExtensions(final Class<?> clazz) {
        return ServiceLoader.load(clazz);
    }
}
