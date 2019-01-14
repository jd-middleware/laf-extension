package com.jd.laf.extension;

/**
 * 扩展点接口
 *
 * @param <T>
 * @param <M>
 */
public interface ExtensionPoint<T, M> {

    /**
     * 获取指定扩展实现
     *
     * @param name
     * @return
     */
    T get(final M name);

    /**
     * 选择一个实现
     *
     * @return
     */
    T get();

    /**
     * 记录数
     *
     * @return
     */
    int size();

    /**
     * 获取扩展实现列表
     *
     * @return
     */
    Iterable<T> extensions();

    /**
     * 反序获取扩展实现列表
     *
     * @return
     */
    Iterable<T> reverse();

    /**
     * 扩展元数据迭代
     *
     * @return
     */
    Iterable<ExtensionMeta<T, M>> metas();

    /**
     * 扩展元数据迭代
     *
     * @param name 名称
     * @return
     */
    Iterable<ExtensionMeta<T, M>> metas(M name);

    /**
     * 获取扩展元数据
     *
     * @param name 名称
     * @return
     */
    ExtensionMeta<T, M> meta(M name);

    /**
     * 扩展点名称
     *
     * @return
     */
    Name<T, String> getName();
}
