package com.jd.laf.extension;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 指定接口的扩展点
 */
public class ExtensionSpi<T, M> implements ExtensionPoint<T, M> {

    //按照名称分组聚合的扩展元数据
    protected ConcurrentMap<M, List<ExtensionMeta<T, M>>> multiNames;
    //按照名称覆盖的扩展元数据
    protected ConcurrentMap<M, ExtensionMeta<T, M>> names;
    //扩展元数据列表
    protected List<ExtensionMeta<T, M>> metas;
    //扩展点名称
    protected Name<T, String> name;
    //缓存默认插件单例实例
    protected T target;
    //比较器
    protected Comparator<ExtensionMeta<T, M>> comparator;
    //分类器
    protected Classify<T, M> classify;
    //是否都是单例
    protected boolean singleton = true;

    //缓存的插件列表
    protected volatile List<T> extensions;
    //缓存的插件反序遍历
    protected volatile List<T> reverses;

    public ExtensionSpi(final Name<T, String> name, final List<ExtensionMeta<T, M>> metas,
                        final Comparator<ExtensionMeta<T, M>> comparator, final Classify<T, M> classify) {
        this.name = name;
        int size = metas.size() + 10;
        this.metas = new LinkedList<ExtensionMeta<T, M>>();
        this.names = new ConcurrentHashMap<M, ExtensionMeta<T, M>>(size);
        this.multiNames = new ConcurrentHashMap<M, List<ExtensionMeta<T, M>>>(size);
        this.comparator = comparator;
        this.classify = classify;
        for (ExtensionMeta<T, M> meta : metas) {
            add(meta);
        }
    }

    protected void add(final ExtensionMeta<T, M> meta) {
        if (meta == null) {
            return;
        }
        if (!meta.isSingleton()) {
            singleton = false;
        }
        List<ExtensionMeta<T, M>> metas;
        List<ExtensionMeta<T, M>> exists;
        //扩展名称
        M name = meta.getExtension().getName();
        if (name != null) {
            //防止被覆盖
            names.putIfAbsent(name, meta);
            metas = multiNames.get(name);
            if (metas == null) {
                metas = new CopyOnWriteArrayList<ExtensionMeta<T, M>>();
                exists = multiNames.putIfAbsent(name, metas);
                if (exists != null) {
                    metas = exists;
                }
            }
            metas.add(meta);
        }
        this.metas.add(meta);
    }

    protected T getObject(final ExtensionMeta<T, M> extension) {
        return extension == null ? null : extension.getTarget();
    }

    @Override
    public T get(final M name) {
        return name == null ? null : getObject(names.get(name));
    }

    @Override
    public T get() {
        if (target == null && !metas.isEmpty()) {
            ExtensionMeta<T, M> meta = metas.get(0);
            if (meta != null) {
                if (meta.isSingleton()) {
                    target = meta.getTarget();
                } else {
                    return meta.getTarget();
                }
            }
        }
        return target;
    }

    @Override
    public Iterable<ExtensionMeta<T, M>> metas() {
        return metas;
    }

    @Override
    public Iterable<ExtensionMeta<T, M>> metas(M name) {
        return name == null ? null : multiNames.get(name);
    }

    @Override
    public ExtensionMeta<T, M> meta(final M name) {
        return name == null ? null : names.get(name);
    }

    @Override
    public int size() {
        return metas.size();
    }

    /**
     * 构造扩展点列表
     *
     * @return
     */
    protected List<T> doExtensions() {
        LinkedList<T> result = new LinkedList<T>();
        T object;
        for (ExtensionMeta<T, M> extension : metas) {
            object = getObject(extension);
            if (object != null) {
                result.add(object);
            }
        }
        return result;
    }

    @Override
    public Iterable<T> extensions() {
        if (singleton) {
            //单例可以缓存
            if (extensions == null) {
                synchronized (this) {
                    if (extensions == null) {
                        extensions = doExtensions();
                    }
                }
            }
            return extensions;
        }
        return doExtensions();
    }

    /**
     * 反序列表
     * @return
     */
    protected List<T> doReverses() {
        LinkedList<T> result = new LinkedList<T>();
        T object;
        for (ExtensionMeta<T, M> extension : metas) {
            object = getObject(extension);
            if (object != null) {
                result.addFirst(object);
            }
        }
        return result;
    }

    @Override
    public Iterable<T> reverse() {
        if (singleton) {
            //单例可以缓存
            if (reverses == null) {
                synchronized (this) {
                    if (reverses == null) {
                        reverses = doReverses();
                    }
                }
            }
            return reverses;
        }
        return doReverses();
    }

    @Override
    public Name<T, String> getName() {
        return name;
    }
}
