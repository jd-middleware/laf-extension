package com.jd.laf.extension;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 扩展点管理
 */
public class ExtensionManager {
    public static ExtensionManager INSTANCE = new ExtensionManager();

    // 扩展点名称
    protected ConcurrentMap<Object, ExtensionSpi> names = new ConcurrentHashMap<Object, ExtensionSpi>();
    // 扩展点
    protected ConcurrentMap<Class, ExtensionSpi> extensions = new ConcurrentHashMap<Class, ExtensionSpi>();
    // 默认加载器
    protected ExtensionLoader loader = SpiLoader.INSTANCE;

    public ExtensionManager() {
    }

    public ExtensionManager(ExtensionScanner scanner, ExtensionLoader loader) {
        if (loader != null) {
            this.loader = loader;
        }
        add(scanner);
    }

    public ExtensionManager(ExtensionLoader loader) {
        if (loader != null) {
            this.loader = loader;
        }
    }

    public ExtensionManager(Collection<Class<?>> extensibles) {
        add(extensibles, loader);
    }

    public ExtensionManager(Collection<Class<?>> extensibles, ExtensionLoader loader) {
        if (loader != null) {
            this.loader = loader;
        }
        add(extensibles, loader);
    }

    protected ExtensionSpi add(final ExtensionSpi extensionSpi) {
        if (extensionSpi != null) {
            Name name = extensionSpi.getName();
            //防止并发
            ExtensionSpi exists = extensions.putIfAbsent(name.getClazz(), extensionSpi);
            if (exists == null) {
                if (name.getName() != null) {
                    names.put(name.getName(), extensionSpi);
                }
                return extensionSpi;
            }
            return exists;
        }
        return null;
    }

    public ExtensionSpi add(final Class<?> extensible) {
        return add(extensible, loader, null);
    }

    public ExtensionSpi add(final Class<?> extensible, final ExtensionLoader loader) {
        return add(extensible, loader, null);
    }

    public ExtensionSpi add(final Class<?> extensible, final ExtensionLoader loader, final Comparator<ExtensionMeta> comparator) {
        if (extensible != null) {
            //判断是否重复添加
            ExtensionSpi extensionSpi = getExtensionSpi(extensible);
            if (extensionSpi == null) {
                ExtensionLoader extensionLoader = loader == null ? this.loader : loader;
                extensionSpi = extensionLoader.load(extensible, comparator);
                if (extensionSpi != null) {
                    return add(extensionSpi);
                }
            }
            return extensionSpi;
        }
        return null;
    }

    public void add(final Collection<Class<?>> extensibles) {
        add(extensibles, loader);
    }

    public void add(final Collection<Class<?>> extensibles, final ExtensionLoader loader) {
        if (extensibles != null) {
            for (Class extensible : extensibles) {
                add(extensible, loader, null);
            }
        }
    }

    public void add(final ExtensionScanner scanner) {
        if (scanner != null) {
            Set<Class> extensions = scanner.scan();
            if (extensions != null) {
                for (Class extension : extensions) {
                    add(extension, loader, null);
                }
            }
        }
    }

    /**
     * 获取扩展实现
     *
     * @param type 类型
     * @param name 扩展名称
     * @param <T>
     * @return
     */
    public <T> T getExtension(final String type, final Object name) {
        ExtensionSpi extensionSpi = names.get(type);
        return extensionSpi == null ? null : (T) extensionSpi.getExtension(name);
    }

    /**
     * 获取扩展实现
     *
     * @param extensible 扩展点类
     * @param name       扩展名称
     * @param <T>        扩展实现
     * @return
     */
    public <T> T getExtension(final Class<T> extensible, final Object name) {
        ExtensionSpi extensionSpi = getExtensionSpi(extensible);
        return extensionSpi == null ? null : (T) extensionSpi.getExtension(name);
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
        ExtensionSpi extensionSpi = getExtensionSpi(extensible);
        return extensionSpi == null ? null : (List<T>) extensionSpi.getExtensions();
    }

    /**
     * 获取扩展点实现
     *
     * @param type 类型
     * @param <T>
     * @return
     */
    public <T> List<T> getExtensions(final String type) {
        ExtensionSpi extensionSpi = names.get(type);
        return extensionSpi == null ? null : (List<T>) extensionSpi.getExtensions();
    }

    /**
     * 获取插件接口
     *
     * @param extensible
     * @return
     */
    public ExtensionSpi getExtensionSpi(final Class extensible) {
        ExtensionSpi spi = extensions.get(extensible);
        if (spi == null) {
            String type = getExtensible(extensible);
            if (type == null) {
                return null;
            }
            spi = names.get(type);
        }
        return spi;
    }

    /**
     * 初始化扫描插件并自动加载
     */
    public static void load() {
        load(null, null);
    }

    /**
     * 初始化扫描插件并加载
     *
     * @param loader
     */
    public static void load(final ExtensionLoader loader) {
        load(null, loader);
    }

    /**
     * 初始化扫描插件并加载
     *
     * @param scanner
     * @param loader
     */
    public static void load(final ExtensionScanner scanner, final ExtensionLoader loader) {
        if (loader != null && loader != INSTANCE.loader) {
            INSTANCE = new ExtensionManager(loader);
        }
        INSTANCE.add(scanner != null ? scanner : new ExtensionScanner.DefaultScanner());
    }

}
