package com.jd.laf.extension;

/**
 * 扩展点接口
 *
 * @param <T>
 * @param <M>
 */
public interface ExtensionPoint<T, M> {

    /**
     * 按照名称获取指定扩展实现，字符串名称后面加上"@供应商"来优先获取指定供应商的扩展
     *
     * @param name 扩展名称
     * @return
     */
    T get(M name);

    /**
     * 按照指定名称或候选名称获取扩展
     *
     * @param name   扩展名称
     * @param option 候选名称
     * @return
     */
    T get(M name, M option);

    /**
     * 按照指定名称获取扩展，如果股存在则返回优先级最高的扩展
     *
     * @param name 扩展名称
     * @return
     */
    T getOrDefault(M name);

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
     * 获取扩展元数据，字符串名称后面加上"@供应商"来优先获取指定供应商的扩展元数据
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
