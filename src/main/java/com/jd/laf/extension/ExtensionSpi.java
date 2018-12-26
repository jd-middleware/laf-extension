package com.jd.laf.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 指定接口的扩展点
 */
public class ExtensionSpi {

    // 扩展点名称
    private ConcurrentMap<Object, ExtensionMeta> names;
    // 扩展点集合
    private List<ExtensionMeta> extensions;
    // 可扩展接口名称
    private Name name;

    public ExtensionSpi(final Name name, final List<ExtensionMeta> extensions) {
        this.name = name;
        this.extensions = new CopyOnWriteArrayList<ExtensionMeta>(extensions);
        this.names = new ConcurrentHashMap<Object, ExtensionMeta>(extensions.size() + 10);
        Name extension;
        for (ExtensionMeta meta : extensions) {
            extension = meta.getExtension();
            if (extension != null && extension.getName() != null) {
                names.put(extension.getName(), meta);
            }
        }
    }

    protected Object getObject(final ExtensionMeta extension) {
        if (extension == null) {
            return null;
        } else if (extension.isSingleton()) {
            if (extension.getTarget() == null) {
                synchronized (extension) {
                    if (extension.getTarget() == null) {
                        extension.setTarget(newInstance(extension.getExtension().getClazz()));
                    }
                }
            }
            return extension.getTarget();
        } else {
            return newInstance(extension.getExtension().getClazz());
        }
    }

    protected Object newInstance(final Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public <T> T getExtension(final Object name) {
        ExtensionMeta meta = names.get(name);
        return (T) getObject(meta);
    }

    public <T> List<T> getExtensions() {
        List<T> result = new ArrayList<T>(extensions.size());
        Object object;
        for (ExtensionMeta extension : extensions) {
            object = getObject(extension);
            if (object != null) {
                result.add((T) object);
            }
        }
        return result;
    }

    /**
     * 动态添加扩展点
     *
     * @param extensible
     * @param name
     * @param target
     */
    public boolean addExtension(final Class extensible, final Object name, final Object target) {
        if (extensible == null || name == null || target == null) {
            return false;
        }
        Class<?> targetClass = target.getClass();
        ExtensionMeta meta = new ExtensionMeta();
        meta.setTarget(target);
        meta.setOrder(Ordered.ORDER);
        meta.setName(new Name(targetClass));
        meta.setExtensible(new Name(extensible));
        meta.setExtension(new Name(targetClass, name));
        meta.setSingleton(true);
        extensions.add(meta);
        names.put(name, meta);
        return true;
    }

    public Name getName() {
        return name;
    }
}
