package com.jd.laf.extension;

/**
 * 扩展点元数据
 */
public class ExtensionMeta<T, M> {
    //实例元数据
    protected Name<? extends T, String> name;
    //实例化接口
    protected Instance instance;
    //是否是单例
    protected boolean singleton = true;
    //单例
    protected T target;
    //扩展点名称
    protected Name<T, String> extensible;
    //扩展实现名称
    protected Name<? extends T, M> extension;
    //顺序
    protected int order;

    public Name<? extends T, String> getName() {
        return name;
    }

    public void setName(Name<? extends T, String> name) {
        this.name = name;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public T getTarget() {
        if (singleton) {
            if (target == null) {
                synchronized (this) {
                    if (target == null) {
                        target = instance.newInstance(name);
                    }
                }
            }
            return target;
        }
        return instance.newInstance(name);
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public Name<T, String> getExtensible() {
        return extensible;
    }

    public void setExtensible(Name<T, String> extensible) {
        this.extensible = extensible;
    }

    public Name<? extends T, M> getExtension() {
        return extension;
    }

    public void setExtension(Name<? extends T, M> extension) {
        this.extension = extension;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
