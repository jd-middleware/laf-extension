package com.jd.laf.extension;

/**
 * 分类算法
 */
public interface Classify<T, M> {

    /**
     * 获取类型
     *
     * @param obj
     * @return
     */
    M type(T obj);

}
