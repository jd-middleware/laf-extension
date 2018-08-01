package com.jd.laf.extension;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 扩展点管理
 */
public class ExtensionManager {
    private static ExtensionManager INSTANCE = new ExtensionManager();

    // 扩展点名称
    private Map<String, ExtensionSpi> names = new ConcurrentHashMap<String, ExtensionSpi>();
    // 扩展点
    private Map<Class, ExtensionSpi> extensions = new ConcurrentHashMap<Class, ExtensionSpi>();
    // 扩展点集合
    private Set<Class> extensibles = new ConcurrentSkipListSet<Class>();
    // 默认加载器
    private ExtensionLoader loader = SpiLoader.INSTANCE;

    public ExtensionManager() {
    }

    public ExtensionManager(ExtensionScanner scanner) {
        if (scanner != null) {
            List<ExtensionSpi> extensionSpis = scanner.scan();
            if (extensionSpis != null) {
                for (ExtensionSpi extensionSpi : extensionSpis) {
                    add(extensionSpi);
                }
            }
        }
    }

    public ExtensionManager(ExtensionLoader loader) {
        if (loader != null) {
            this.loader = loader;
        }
    }

    public ExtensionManager(Collection<Class<?>> extensibles) {
        add(extensibles);
    }

    public ExtensionManager(Collection<Class<?>> extensibles, ExtensionLoader loader) {
        add(extensibles, loader);
    }

    protected void add(final ExtensionSpi extensionSpi) {
        if (extensionSpi != null) {
            Name name = extensionSpi.getName();
            extensions.put(name.getClazz(), extensionSpi);
            if (name.getName() != null && !name.getName().isEmpty()) {
                names.put(name.getName(), extensionSpi);
            }
        }
    }

    public void add(final Class<?> extensible) {
        add(extensible, loader);
    }

    public void add(final Class<?> extensible, final ExtensionLoader loader) {
        if (extensible != null) {
            ExtensionLoader extensionLoader = loader == null ? this.loader : loader;
            ExtensionSpi extensionSpi = extensionLoader.load(extensible);
            if (extensionSpi != null) {
                add(extensionSpi);
            }
        }
    }

    public void add(final Collection<Class<?>> extensibles) {
        add(extensibles, loader);
    }

    public void add(final Collection<Class<?>> extensibles, final ExtensionLoader loader) {
        if (extensibles != null) {
            for (Class extensible : extensibles)
                add(extensible, loader);
        }
    }

    public Object getExtension(final String type, final String name) {
        ExtensionSpi extensionSpi = names.get(type);
        return extensionSpi == null ? null : extensionSpi.getExtension(name);
    }

    /**
     * 获取扩展实现
     *
     * @param type  类型
     * @param name  扩展名称
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getExtension(final String type, final String name, final Class<T> clazz) {
        ExtensionSpi extensionSpi = names.get(type);
        return extensionSpi == null ? null : extensionSpi.getExtension(name, clazz);
    }

    /**
     * 获取扩展实现
     *
     * @param extensible 扩展点类
     * @param name       扩展名称
     * @param <T>        扩展实现
     * @return
     */
    public <T> T getExtension(final Class<T> extensible, final String name) {
        String type = getExtensible(extensible);
        if (type == null) return null;
        ExtensionSpi extensionSpi = names.get(type);
        return extensionSpi == null ? null : extensionSpi.getExtension(name, extensible);
    }

    /**
     * 获取注解
     *
     * @param extensible
     * @param <T>
     * @return
     */
    protected <T> String getExtensible(final Class<T> extensible) {
        if (extensible == null) {
            return null;
        }
        String type = null;
        Extensible annotation = extensible.getAnnotation(Extensible.class);
        if (annotation != null) {
            type = annotation.value();
        }
        if (type == null || type.isEmpty()) {
            type = extensible.getName();
        }
        return type;
    }

    /**
     * 获取扩展实现
     *
     * @param extensible 扩展点类型
     * @param <T>
     * @return
     */
    public <T> List<T> getExtensions(final Class<T> extensible) {
        String type = getExtensible(extensible);
        if (type == null) return null;
        ExtensionSpi extensionSpi = names.get(type);
        return extensionSpi == null ? null : extensionSpi.getExtensions(extensible);
    }

    /**
     * 获取扩展点实现
     *
     * @param type 类型
     * @return
     */
    public List<Object> getExtensions(final String type) {
        ExtensionSpi extensionSpi = names.get(type);
        return extensionSpi == null ? null : extensionSpi.getExtensions();
    }

    /**
     * 获取扩展点实现
     *
     * @param type  类型
     * @param clazz 转换的类型
     * @param <T>
     * @return
     */
    public <T> List<T> getExtensions(final String type, final Class<T> clazz) {
        ExtensionSpi extensionSpi = names.get(type);
        return extensionSpi == null ? null : extensionSpi.getExtensions(clazz);
    }

    public static ExtensionManager getInstance() {
        return INSTANCE;
    }

}
