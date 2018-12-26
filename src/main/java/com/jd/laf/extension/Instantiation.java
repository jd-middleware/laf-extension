package com.jd.laf.extension;

/**
 * 实例描述信息
 */
public class Instantiation {

    //实例名称
    protected Name name;
    //是否是单例
    protected boolean singleton = true;
    //单例
    protected Object target;
    //实例化
    protected Instance instance;

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public <T> T getTarget() {
        if (isSingleton()) {
            if (target == null) {
                synchronized (this) {
                    if (target == null) {
                        target = instance.newInstance(name);
                    }
                }
            }
            return (T) target;
        }
        return instance.newInstance(name);
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }
}
