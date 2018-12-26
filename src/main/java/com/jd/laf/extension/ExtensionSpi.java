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
    private Map<Object, ExtensionMeta> names = new HashMap<Object, ExtensionMeta>();
    // 扩展点集合
    private List<ExtensionMeta> extensions;
    // 可扩展接口名称
    private Name name;

    public ExtensionSpi(final Name name, final List<ExtensionMeta> extensions) {
        this.extensions = extensions;
        this.name = name;
        if (extensions != null) {
            Name extension;
            for (ExtensionMeta meta : extensions) {
                extension = meta.getExtension();
                if (extension != null && extension.getName() != null) {
                    names.put(extension.getName(), meta);
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

    public Name getName() {
        return name;
    }
}
