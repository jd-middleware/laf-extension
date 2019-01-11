package com.jd.laf.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 指定接口的扩展点
 */
public class ExtensionSpi<T, M> implements ExtensionPoint<T, M> {

    //扩展实现
    protected ConcurrentMap<M, List<ExtensionMeta<T, M>>> multiNames;
    //扩展实现
    protected ConcurrentMap<M, ExtensionMeta<T, M>> names;
    //扩展实现
    protected List<ExtensionMeta<T, M>> extensions;
    //扩展点名称
    protected Name<T, String> name;
    //缓存默认插件单例实例
    protected T target;

    public ExtensionSpi(final Name<T, String> name, final List<ExtensionMeta<T, M>> extensions) {
        this.name = name;
        int size = extensions.size() + 10;
        this.extensions = new CopyOnWriteArrayList<ExtensionMeta<T, M>>();
        this.names = new ConcurrentHashMap<M, ExtensionMeta<T, M>>(size);
        this.multiNames = new ConcurrentHashMap<M, List<ExtensionMeta<T, M>>>(size);
        for (ExtensionMeta<T, M> meta : extensions) {
            add(meta);
        }
    }

    protected void add(final ExtensionMeta<T, M> meta) {
        if (meta == null) {
            return;
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
        extensions.add(meta);
    }

    protected T getObject(final ExtensionMeta<T, M> extension) {
        return extension == null ? null : extension.getTarget();
    }

    @Override
    public T get(final M name) {
        return getObject(names.get(name));
    }

    @Override
    public T get() {
        if (target == null && !extensions.isEmpty()) {
            ExtensionMeta<T, M> meta = extensions.get(0);
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
        return extensions;
    }

    @Override
    public Iterable<ExtensionMeta<T, M>> metas(M name) {
        return multiNames.get(name);
    }

    @Override
    public ExtensionMeta<T, M> meta(final M name) {
        return names.get(name);
    }

    @Override
    public Iterable<T> extensions() {
        List<T> result = new LinkedList<T>();
        extensions(result);
        return result;
    }

    protected void extensions(final List<T> result) {
        T object;
        for (ExtensionMeta<T, M> extension : extensions) {
            object = getObject(extension);
            if (object != null) {
                result.add(object);
            }
        }
    }

    @Override
    public Iterable<T> reverse() {
        List<T> result = new ArrayList<T>(20);
        extensions(result);
        Collections.reverse(result);
        return result;
    }

    @Override
    public Name<T, String> getName() {
        return name;
    }
}
