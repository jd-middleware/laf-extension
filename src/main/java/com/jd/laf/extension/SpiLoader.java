package com.jd.laf.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

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
        Name extensibleName = new Name(clazz, extensible != null ? extensible.value() : null);
        List<ExtensionMeta> metas = new ArrayList<ExtensionMeta>();
        ServiceLoader<?> loader = ServiceLoader.load(clazz, clazz.getClassLoader());
        ExtensionMeta meta;
        Extension extension;
        Scope scope;
        Class<?> serviceClass;
        for (Object service : loader) {
            serviceClass = service.getClass();
            extension = serviceClass.getAnnotation(Extension.class);
            scope = serviceClass.getAnnotation(Scope.class);

            meta = new ExtensionMeta();
            meta.setExtensible(extensibleName);
            meta.setTarget(service);
            meta.setExtension(new Name(serviceClass, extension == null ? null : extension.value()));
            meta.setSingleton(scope == null ? true : scope.singleton());
            metas.add(meta);
        }
        return new ExtensionSpi(extensibleName, metas);
    }
}
