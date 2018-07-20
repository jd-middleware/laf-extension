package com.jd.laf.extension;

/**
 * 扩展点加载器
 */
public interface ExtensionLoader {

    /**
     * 加载扩展点
     *
     * @param extensible 可扩展的接口
     * @return 扩展点
     */
    ExtensionSpi load(Class<?> extensible);
}
