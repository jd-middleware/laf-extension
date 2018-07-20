package com.jd.laf.extension;

/**
 * 扩展名称
 */
public class Name {
    // 类型
    private Class clazz;
    // 名称
    private String name;

    public Name(Class clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }
}
