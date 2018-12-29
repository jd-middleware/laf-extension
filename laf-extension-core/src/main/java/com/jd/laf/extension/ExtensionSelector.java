package com.jd.laf.extension;

/**
 * 扩展点选择器
 *
 * @param <T>
 * @param <M>
 * @param <C>
 * @param <K>
 */
public class ExtensionSelector<T, M, C, K> {

    ExtensionPoint<T, M> extensionPoint;

    Selector<T, M, C, K> selector;

    public ExtensionSelector(ExtensionPoint<T, M> extensionPoint, Selector<T, M, C, K> selector) {
        this.extensionPoint = extensionPoint;
        this.selector = selector;
    }

    /**
     * 选择
     *
     * @param condition 条件
     * @return
     */
    public K select(final C condition) {
        return selector.select(extensionPoint, condition);
    }

}
