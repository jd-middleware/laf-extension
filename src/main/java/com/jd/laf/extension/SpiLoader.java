package com.jd.laf.extension;

import java.util.*;

/**
 * SPI加载插件
 */
public class SpiLoader implements ExtensionLoader {

    public static final SpiLoader INSTANCE = new SpiLoader();

    @Override
    public ExtensionSpi load(final Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        Extensible extensible = clazz.getAnnotation(Extensible.class);
        Name extensibleName = new Name(clazz, extensible != null && extensible.value() != null
                && !extensible.value().isEmpty() ? extensible.value() : clazz.getName());
        List<ExtensionMeta> metas = new ArrayList<ExtensionMeta>();
        Iterable<?> loader = loadExtensions(clazz);
        Class<?> serviceClass;
        Object target;
        Instance instance;
        Name name;
        //遍历扩展点
        for (Object service : loader) {
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
                    ((Ordered) target).order() : (extension == null ? Short.MAX_VALUE : extension.order()));
            meta.setInstance(instance != null ? instance : Instance.ClazzInstance.INSTANCE);
            meta.setName(name);
            metas.add(meta);
        }

        //按照顺序升序排序
        Collections.sort(metas, new Comparator<ExtensionMeta>() {
            @Override
            public int compare(ExtensionMeta o1, ExtensionMeta o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });

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
