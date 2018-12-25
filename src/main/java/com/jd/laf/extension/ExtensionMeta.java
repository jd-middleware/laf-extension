package com.jd.laf.extension;

/**
 * 扩展点元数据
 */
public class ExtensionMeta extends Instantiation {
    //可扩展接口名称
    protected Name extensible;
    //扩展点名称
    protected Name extension;
    //顺序
    protected int order;

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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
