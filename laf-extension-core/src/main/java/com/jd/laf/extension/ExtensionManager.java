package com.jd.laf.extension;

import com.jd.laf.extension.ExtensionLoader.Wrapper;
import com.jd.laf.extension.ExtensionMeta.AscendingComparator;
import com.jd.laf.extension.Instantiation.ClazzInstance;
import com.jd.laf.extension.listener.ExtensionListener;
import com.jd.laf.extension.listener.LoaderEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Collections.sort;

/**
 * 扩展点管理
 */
public class ExtensionManager {
    public static final ExtensionManager INSTANCE = new ExtensionManager();

    //扩展点快照
    protected volatile Snapshot snapshot;

    protected List<ExtensionListener> listeners = new CopyOnWriteArrayList<ExtensionListener>();

    public ExtensionManager() {
        this(SpiLoader.INSTANCE);
    }

    public ExtensionManager(ExtensionScanner scanner, ExtensionLoader loader) {
        this(loader);
        loadExtension(scanner);
    }

    public ExtensionManager(ExtensionLoader loader) {
        snapshot = new Snapshot(loader);
    }

    public ExtensionManager(Collection<Class<?>> extensibles) {
        this(SpiLoader.INSTANCE);
        loadExtension(extensibles, null);
    }

    public ExtensionManager(Collection<Class<?>> extensibles, ExtensionLoader loader) {
        this(loader);
        loadExtension(extensibles, loader);
    }

    public <T, M> ExtensionPoint<T, M> getOrLoadExtensionPoint(final Class<T> extensible) {
        return snapshot.getOrLoadExtensionPoint(extensible, null, AscendingComparator.INSTANCE, null);
    }

    public <T, M> ExtensionPoint<T, M> getOrLoadExtensionPoint(final Class<T> extensible, final ExtensionLoader loader) {
        return snapshot.getOrLoadExtensionPoint(extensible, loader, AscendingComparator.INSTANCE, null);
    }

    public <T, M> ExtensionPoint<T, M> getOrLoadExtensionPoint(final Class<T> extensible, final Comparator<ExtensionMeta<T, M>> comparator) {
        return snapshot.getOrLoadExtensionPoint(extensible, null, comparator, null);
    }

    public <T, M> ExtensionPoint<T, M> getOrLoadExtensionPoint(final Class<T> extensible, final ExtensionLoader loader, final Comparator<ExtensionMeta<T, M>> comparator) {
        return snapshot.getOrLoadExtensionPoint(extensible, loader, comparator, null);
    }

    public <T, M> ExtensionPoint getOrLoadExtensionPoint(final Class<T> extensible,
                                                         final ExtensionLoader loader,
                                                         final Comparator<ExtensionMeta<T, M>> comparator,
                                                         final Classify<T, M> classify) {
        return snapshot.getOrLoadExtensionPoint(extensible, loader, comparator, classify);
    }

    public void loadExtension(final Collection<Class<?>> extensibles) {
        snapshot.loadExtension(extensibles);
    }

    public void loadExtension(final Collection<Class<?>> extensibles, final ExtensionLoader loader) {
        snapshot.loadExtension(extensibles, loader);
    }

    public void loadExtension(final ExtensionScanner scanner) {
        loadExtension(scanner, null);
    }

    public void loadExtension(final ExtensionScanner scanner, final ExtensionLoader loader) {
        if (scanner != null) {
            loadExtension(scanner.scan(), loader);
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
    public <T, M> T getExtension(final String type, final M name) {
        return snapshot.getExtension(type, name);
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
        ExtensionPoint spi = getExtensionPoint(extensible);
        return spi == null ? null : (T) spi.get(name);
    }

    /**
     * 获取或加载扩展实现
     *
     * @param extensible 扩展点类
     * @param name       扩展名称
     * @param <T>        扩展实现
     * @return
     */
    public <T, M> T getOrLoadExtension(final Class<T> extensible, final M name) {
        ExtensionPoint<T, M> spi = getOrLoadExtensionPoint(extensible);
        return spi == null ? null : spi.get(name);
    }

    /**
     * 获取或加载扩展实现，并返回第一个实现
     *
     * @param extensible 扩展点类
     * @param <T>        扩展实现
     * @return
     */
    public <T, M> T getOrLoadExtension(final Class<T> extensible) {
        ExtensionPoint<T, M> spi = getOrLoadExtensionPoint(extensible);
        return spi == null ? null : spi.get();
    }

    /**
     * 获取扩展实现
     *
     * @param extensible 扩展点类型
     * @param <T>
     * @return
     */
    public <T, M> Iterable<T> getExtensions(final Class<T> extensible) {
        ExtensionPoint<T, M> spi = getExtensionPoint(extensible);
        return spi == null ? null : spi.extensions();
    }

    /**
     * 获取扩展实现
     *
     * @param extensible 扩展点类型
     * @param <T>
     * @return
     */
    public <T> Iterable<T> getOrLoadExtensions(final Class<T> extensible) {
        ExtensionPoint<T, ?> spi = getOrLoadExtensionPoint(extensible);
        return spi == null ? null : spi.extensions();
    }

    /**
     * 获取扩展点实现
     *
     * @param type 类型
     * @param <T>
     * @return
     */
    public <T, M> Iterable<T> getExtensions(final String type) {
        return snapshot.getExtensions(type);
    }

    /**
     * 获取插件接口
     *
     * @param extensible
     * @return
     */
    public <T, M> ExtensionPoint<T, M> getExtensionPoint(final Class<T> extensible) {
        return snapshot.getExtensionPoint(extensible);
    }

    /**
     * 添加监听器
     *
     * @param listener
     * @return
     */
    public boolean add(final ExtensionListener listener) {
        if (listener == null) {
            return false;
        }
        return listeners.add(listener);
    }

    /**
     * 获取扩展实现
     *
     * @param type 类型
     * @param name 扩展名称
     * @param <T>
     * @param <M>
     * @return
     */
    public static <T, M> T get(final String type, final M name) {
        return INSTANCE.getExtension(type, name);
    }

    /**
     * 获取扩展实现
     *
     * @param extensible 扩展点类
     * @param name       扩展名称
     * @param <T>
     * @param <M>
     * @return
     */
    public static <T, M> T get(final Class<T> extensible, final M name) {
        return INSTANCE.getExtension(extensible, name);
    }

    /**
     * 获取或加载扩展实现
     *
     * @param extensible 扩展点类
     * @param name       扩展名称
     * @param <T>
     * @param <M>
     * @return
     */
    public static <T, M> T getOrLoad(final Class<T> extensible, final M name) {
        return INSTANCE.getOrLoadExtension(extensible, name);
    }

    /**
     * 获取或加载扩展实现
     *
     * @param extensible 扩展点类
     * @param <T>        扩展实现
     * @return
     */
    public static <T> T getOrLoad(final Class<T> extensible) {
        return INSTANCE.getOrLoadExtension(extensible);
    }

    /**
     * 获取扩展实现
     *
     * @param extensible 扩展点类型
     * @param <T>
     * @return
     */
    public static <T> Iterable<T> get(final Class<T> extensible) {
        return INSTANCE.getExtensions(extensible);
    }

    /**
     * 获取扩展实现
     *
     * @param extensible 扩展点类型
     * @param <T>
     * @return
     */
    public static <T> Iterable<T> getOrLoadAll(final Class<T> extensible) {
        return INSTANCE.getOrLoadExtensions(extensible);
    }

    /**
     * 获取扩展点实现
     *
     * @param type 类型
     * @param <T>
     * @return
     */
    public static <T> Iterable<T> get(final String type) {
        return INSTANCE.getExtensions(type);
    }

    /**
     * 获取插件接口
     *
     * @param extensible
     * @return
     */
    public static <T, M> ExtensionPoint<T, M> getSpi(final Class<T> extensible) {
        return INSTANCE.getExtensionPoint(extensible);
    }

    public static <T, M> ExtensionPoint<T, M> getOrLoadSpi(final Class<T> extensible) {
        return INSTANCE.getOrLoadExtensionPoint(extensible);
    }

    public static <T, M> ExtensionPoint<T, M> getOrLoadSpi(final Class<T> extensible, final ExtensionLoader loader) {
        return INSTANCE.getOrLoadExtensionPoint(extensible, loader);
    }

    public static <T, M> ExtensionPoint<T, M> getOrLoadSpi(final Class<T> extensible,
                                                           final Comparator<ExtensionMeta<T, M>> comparator) {
        return INSTANCE.getOrLoadExtensionPoint(extensible, comparator);
    }

    public static <T, M> ExtensionPoint<T, M> getOrLoadSpi(final Class<T> extensible,
                                                           final ExtensionLoader loader,
                                                           final Comparator<ExtensionMeta<T, M>> comparator) {
        return INSTANCE.getOrLoadExtensionPoint(extensible, loader, comparator);
    }

    public static <T, M> ExtensionPoint<T, M> getOrLoadSpi(final Class<T> extensible,
                                                           final ExtensionLoader loader,
                                                           final Comparator<ExtensionMeta<T, M>> comparator,
                                                           final Classify<T, M> classify) {
        return INSTANCE.getOrLoadExtensionPoint(extensible, loader, comparator, classify);
    }

    public static void load(final Collection<Class<?>> extensibles) {
        INSTANCE.loadExtension(extensibles);
    }

    public static void load(final Collection<Class<?>> extensibles, final ExtensionLoader loader) {
        INSTANCE.loadExtension(extensibles, loader);
    }

    public static void load(final ExtensionScanner scanner) {
        INSTANCE.loadExtension(scanner);
    }

    /**
     * 初始化扫描插件并加载
     *
     * @param scanner
     * @param loader
     */
    public static void load(final ExtensionScanner scanner, final ExtensionLoader loader) {
        INSTANCE.loadExtension(scanner, loader);
    }

    /**
     * 注册插件加载器
     *
     * @param loader
     * @see ExtensionManager#register(com.jd.laf.extension.ExtensionLoader)
     */
    @Deprecated
    public static void wrap(final ExtensionLoader loader) {
        register(loader);
    }

    /**
     * 注册插件加载器
     *
     * @param loader
     */
    public synchronized static void register(final ExtensionLoader loader) {
        Snapshot old = INSTANCE.snapshot;
        Snapshot snapshot = old.register(loader);
        boolean flag = old == snapshot;
        if (!flag) {
            INSTANCE.snapshot = snapshot;
            for (ExtensionListener listener : INSTANCE.listeners) {
                listener.onEvent(new LoaderEvent(INSTANCE));
            }
        }
    }

    /**
     * 注销插件加载器
     *
     * @param loader
     */
    public synchronized static void deregister(final ExtensionLoader loader) {
        Snapshot old = INSTANCE.snapshot;
        Snapshot snapshot = old.deregister(loader);
        boolean flag = old == snapshot;
        if (!flag) {
            INSTANCE.snapshot = snapshot;
            for (ExtensionListener listener : INSTANCE.listeners) {
                listener.onEvent(new LoaderEvent(INSTANCE));
            }
        }
    }

    /**
     * 添加监听器
     *
     * @param listener
     * @return
     */
    public static boolean addListener(final ExtensionListener listener) {
        return INSTANCE.add(listener);
    }

    /**
     * 指定加载器的扩展点快照
     */
    protected static class Snapshot {

        // 扩展点名称
        protected ConcurrentMap<String, ExtensionSpi> names = new ConcurrentHashMap<String, ExtensionSpi>();
        // 扩展点
        protected ConcurrentMap<Class, ExtensionSpi> extensions = new ConcurrentHashMap<Class, ExtensionSpi>();
        // 加载器
        protected ExtensionLoader loader;

        public Snapshot() {
            this(SpiLoader.INSTANCE);
        }

        public Snapshot(ExtensionLoader loader) {
            this.loader = loader == null ? SpiLoader.INSTANCE : loader;
        }

        protected void addTo(final Set<ExtensionLoader> loaders, final ExtensionLoader loader) {
            if (loader == null) {
                return;
            }
            if (loader instanceof Wrapper) {
                for (ExtensionLoader l : ((Wrapper) loader).loaders) {
                    addTo(loaders, l);
                }
            } else {
                loaders.add(loader);
            }
        }

        /**
         * 注册插件加载器
         *
         * @param loader
         */
        public <T, M> Snapshot register(final ExtensionLoader loader) {
            if (loader == null) {
                return this;
            } else if (loader == this.loader) {
                return this;
            }
            //旧插件
            Set<ExtensionLoader> loaders = new LinkedHashSet<ExtensionLoader>();
            addTo(loaders, this.loader);
            //新插件
            Set<ExtensionLoader> newLoaders = new LinkedHashSet<ExtensionLoader>();
            addTo(newLoaders, loader);
            if (!loaders.addAll(newLoaders)) {
                //已经存在
                return this;
            }
            //新插件加载器
            ExtensionLoader wrapper = new Wrapper(newLoaders);
            //构造数据快照
            Snapshot result = new Snapshot(new Wrapper(loaders));
            ExtensionSpi<T, M> spi;
            Name<T, String> name;
            List<ExtensionMeta<T, M>> metas;
            //遍历已经加载的插件，追加新的插件
            for (Map.Entry<Class, ExtensionSpi> entry : extensions.entrySet()) {
                spi = entry.getValue();
                name = spi.name;
                metas = new LinkedList<ExtensionMeta<T, M>>();
                //原有的插件
                metas.addAll(spi.extensions);
                //加载新插件
                load(name.getClazz(), name, wrapper, spi.classify, metas);
                //排序
                sort(metas, spi.comparator);
                //构造新的扩展点
                result.add(new ExtensionSpi(name, metas, spi.comparator, spi.classify));
            }
            return result;
        }

        /**
         * 注销插件加载器
         *
         * @param loader
         */
        public <T, M> Snapshot deregister(final ExtensionLoader loader) {
            if (loader == null) {
                return this;
            } else if (loader == this.loader) {
                return new Snapshot();
            }
            //旧插件
            Set<ExtensionLoader> loaders = new LinkedHashSet<ExtensionLoader>();
            addTo(loaders, this.loader);
            //删除的插件
            Set<ExtensionLoader> excludes = new LinkedHashSet<ExtensionLoader>();
            addTo(excludes, loader);
            if (!loaders.removeAll(excludes)) {
                //待删除的插件不在旧的插件里面
                return this;
            } else if (loaders.isEmpty()) {
                return new Snapshot();
            }

            //构造数据快照
            Snapshot result = new Snapshot(loaders.size() == 1 ? loaders.iterator().next() : new Wrapper(loaders));
            ExtensionSpi<T, M> spi;
            Name<T, String> name;
            List<ExtensionMeta<T, M>> metas;
            //遍历已经加载的插件，删除注销的插件加载器所加载的插件
            for (Map.Entry<Class, ExtensionSpi> entry : extensions.entrySet()) {
                spi = entry.getValue();
                name = spi.name;
                metas = new LinkedList<ExtensionMeta<T, M>>();
                for (ExtensionMeta<T, M> meta : spi.extensions) {
                    if (!excludes.contains(meta.loader)) {
                        metas.add(meta);
                    }
                }
                //构造新的扩展点
                result.add(new ExtensionSpi(name, metas, spi.comparator, spi.classify));
            }
            return result;

        }

        protected <T, M> ExtensionSpi<T, M> add(final ExtensionSpi<T, M> ExtensionPoint) {
            if (ExtensionPoint != null) {
                Name<T, String> name = ExtensionPoint.getName();
                //防止并发
                ExtensionSpi<T, M> exists = extensions.putIfAbsent(name.getClazz(), ExtensionPoint);
                if (exists == null) {
                    if (name.getName() != null) {
                        names.put(name.getName(), ExtensionPoint);
                    }
                    return ExtensionPoint;
                }
                return exists;
            }
            return null;
        }

        /**
         * 获取扩展点实现
         *
         * @param type 类型
         * @param <T>
         * @return
         */
        public <T, M> Iterable<T> getExtensions(final String type) {
            ExtensionPoint<T, M> spi = names.get(type);
            return spi == null ? null : spi.extensions();
        }

        /**
         * 获取扩展实现
         *
         * @param type 类型
         * @param name 扩展名称
         * @param <T>
         * @return
         */
        public <T, M> T getExtension(final String type, final M name) {
            ExtensionPoint<T, M> spi = names.get(type);
            return spi == null ? null : (T) spi.get(name);
        }

        /**
         * 获取插件接口
         *
         * @param extensible
         * @return
         */
        public <T, M> ExtensionPoint<T, M> getExtensionPoint(final Class<T> extensible) {
            return extensions.get(extensible);
        }

        /**
         * 获取或加载扩展点
         *
         * @param extensible
         * @param loader
         * @param comparator
         * @param classify
         * @param <T>
         * @param <M>
         * @return
         */
        public <T, M> ExtensionPoint getOrLoadExtensionPoint(final Class<T> extensible,
                                                             final ExtensionLoader loader,
                                                             final Comparator<ExtensionMeta<T, M>> comparator,
                                                             final Classify<T, M> classify) {
            if (extensible == null) {
                return null;
            }
            //判断是否重复添加
            ExtensionPoint<T, M> result = getExtensionPoint(extensible);
            if (result == null) {
                //获取扩展点注解
                Extensible annotation = extensible.getAnnotation(Extensible.class);
                //构造扩展点名称
                Name extensibleName = new Name(extensible, annotation != null && annotation.value() != null
                        && !annotation.value().isEmpty() ? annotation.value() : extensible.getName());
                //加载插件
                List<ExtensionMeta<T, M>> metas = new LinkedList<ExtensionMeta<T, M>>();
                load(extensible, extensibleName, loader, classify, metas);
                //排序
                Comparator c = comparator == null ? AscendingComparator.INSTANCE : comparator;
                sort(metas, c);

                result = add(new ExtensionSpi(extensibleName, metas, c, classify));
            }
            return result;
        }

        /**
         * 加载扩展点
         *
         * @param extensible
         * @param extensibleName
         * @param loader
         * @param classify
         * @param metas
         * @param <T>
         * @param <M>
         */
        protected <T, M> void load(final Class<T> extensible, final Name extensibleName, final ExtensionLoader loader,
                                   final Classify<T, M> classify, final List<ExtensionMeta<T, M>> metas) {
            //加载插件
            Collection<Plugin<T>> plugins = loader == null ? this.loader.load(extensible) : loader.load(extensible);
            for (Plugin<T> plugin : plugins) {
                Class<?> pluginClass = plugin.name.getClazz();
                Extension extension = pluginClass.getAnnotation(Extension.class);
                ExtensionMeta<T, M> meta = new ExtensionMeta<T, M>();
                //记录加载器信息，便于卸载加载器
                meta.setLoader(plugin.loader);
                meta.setExtensible(extensibleName);
                meta.setName(plugin.name);
                meta.setInstantiation(plugin.instantiation == null ? ClazzInstance.INSTANCE : plugin.instantiation);
                meta.setTarget(plugin.target);
                meta.setSingleton(plugin.isSingleton() != null ? plugin.isSingleton() :
                        (Prototype.class.isAssignableFrom(pluginClass) ? false :
                                (extension == null ? true : extension.singleton())));
                //获取插件，不存在则创建
                T target = meta.getTarget();
                meta.setExtension(new Name(pluginClass, classify != null ? classify.type(target) :
                        (Type.class.isAssignableFrom(pluginClass) ? ((Type) target).type() :
                                (extension != null && extension.value() != null && !extension.value().isEmpty() ? extension.value() :
                                        pluginClass.getName()))));
                meta.setOrder(Ordered.class.isAssignableFrom(pluginClass) ? ((Ordered) target).order() :
                        (extension == null ? Ordered.ORDER : extension.order()));
                metas.add(meta);

            }
        }

        /**
         * 加载扩展点集合
         *
         * @param extensibles
         */
        public void loadExtension(final Collection<Class<?>> extensibles) {
            loadExtension(extensibles, loader);
        }

        /**
         * 加载扩展点集合
         *
         * @param extensibles
         * @param loader
         */
        public void loadExtension(final Collection<Class<?>> extensibles, final ExtensionLoader loader) {
            if (extensibles != null) {
                ExtensionLoader extensionLoader = loader == null ? this.loader : loader;
                for (Class extensible : extensibles) {
                    getOrLoadExtensionPoint(extensible, extensionLoader, null, null);
                }
            }
        }
    }

}
