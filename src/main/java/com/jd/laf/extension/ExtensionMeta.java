package com.jd.laf.extension;

/**
 * 扩展点元数据
 */
public class ExtensionMeta {
    // 可扩展接口名称
    private Name extensible;
    // 扩展点名称
    private Name extension;
    // 是否是单例
    private boolean singleton = true;
    // 单例
    private Object target;

    public Name getExtensible() {
        return extensible;
    }

    public void setExtensible(Name extensible) {
        this.extensible = extensible;
    }

    public Name getExtension() {
        return extension;
    }

    public void setExtension(Name extension) {
        this.extension = extension;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
