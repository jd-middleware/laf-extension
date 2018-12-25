package com.jd.laf.extension;

/**
 * 扩展名称
 */
public class Name {
    // 类型
    private Class clazz;
    // 名称
    private String name;

    public Name(Class clazz) {
        this.clazz = clazz;
    }

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

    public void setName(String name) {
        this.name = name;
    }
}
