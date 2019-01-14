package com.jd.laf.extension;

/**
 * 扩展点实例
 */
public class Plugin<T> {
    //实例元数据
    protected Name<? extends T, String> name;
    //实例化接口
    protected Instantiation instantiation;
    //是否是单例
    protected Boolean singleton;
    //单例
    protected T target;
    //加载器
    protected Object loader;

    public Plugin() {
    }

    public Plugin(Name<? extends T, String> name, T target, Object loader) {
        this.name = name;
        this.target = target;
        this.loader = loader;
    }

    public Plugin(Name<? extends T, String> name, Instantiation instantiation, Boolean singleton, T target, Object loader) {
        this.name = name;
        this.instantiation = instantiation;
        this.singleton = singleton;
        this.target = target;
        this.loader = loader;
    }

    public Name<? extends T, String> getName() {
        return name;
    }

    public void setName(Name<? extends T, String> name) {
        this.name = name;
    }

    public Boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(Boolean singleton) {
        this.singleton = singleton;
    }

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public Instantiation getInstantiation() {
        return instantiation;
    }

    public void setInstantiation(Instantiation instantiation) {
        this.instantiation = instantiation;
    }

    public Object getLoader() {
        return loader;
    }

    public void setLoader(Object loader) {
        this.loader = loader;
    }
}
