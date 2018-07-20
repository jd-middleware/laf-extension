package com.jd.laf.extension;

import java.util.List;

/**
 * 扩展点加载器
 */
public interface ExtensionScanner {

    /**
     * 加载扩展点
     *
     * @return 扩展点集合
     */
    List<ExtensionSpi> scan();
}
