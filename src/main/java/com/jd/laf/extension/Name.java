package com.jd.laf.extension;

/**
 * 扩展名称
 */
public class Name<T, M> {
    // 类型
    private Class<T> clazz;
    // 名称
    private M name;

    public Name(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Name(Class<T> clazz, M name) {
        this.clazz = clazz;
        this.name = name;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public M getName() {
        return name;
    }

}
