package com.jd.laf.extension;

/**
 * 扩展名称
 */
public class Name {
    // 类型
    private Class clazz;
    // 名称
    private Object name;

    public Name(Class clazz) {
        this.clazz = clazz;
    }

    public Name(Class clazz, Object name) {
        this.clazz = clazz;
        this.name = name;
    }

    public Class getClazz() {
        return clazz;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }
}
