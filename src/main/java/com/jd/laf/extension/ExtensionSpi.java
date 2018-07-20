package com.jd.laf.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 指定接口的扩展点
 */
public class ExtensionSpi {

    // 扩展点名称
    private Map<String, ExtensionMeta> names = new HashMap<String, ExtensionMeta>();
    // 扩展点集合
    private List<ExtensionMeta> extensions;
    // 可扩展接口名称
    private Name name;

    public ExtensionSpi(Name name, List<ExtensionMeta> extensions) {
        this.extensions = extensions;
        this.name = name;
        if (extensions != null) {
            Name extension;
            for (ExtensionMeta meta : extensions) {
                name = meta.getExtension();
                if (name != null && name.getName() != null && !name.getName().isEmpty()) {
                    names.put(name.getName(), meta);
                }
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

    public Object getExtension(final String name) {
        ExtensionMeta meta = names.get(name);
        return getObject(meta);
    }

    public <T> T getExtension(final String name, final Class<T> clazz) {
        return (T) getExtension(name);
    }

    public List<Object> getExtensions() {
        List<Object> result = new ArrayList<Object>(extensions.size());
        Object object;
        for (ExtensionMeta extension : extensions) {
            object = getObject(extension);
            if (object != null) {
                result.add(object);
            }
        }
        return result;
    }

    public <T> List<T> getExtensions(final Class<T> clazz) {
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

    public Name getName() {
        return name;
    }
}
