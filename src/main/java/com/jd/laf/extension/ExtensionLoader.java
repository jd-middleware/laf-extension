package com.jd.laf.extension;

import java.util.Comparator;

/**
 * 扩展点加载器
 */
public interface ExtensionLoader {

    /**
     * 加载扩展点
     *
     * @param extensible 可扩展的接口
     * @param comparator 比较器
     * @return 扩展点
     */
    ExtensionSpi load(Class<?> extensible, Comparator<ExtensionMeta> comparator);


    /**
     * 升序排序
     */
    class AscendingComparator implements Comparator<ExtensionMeta> {

        public static final Comparator INSTANCE = new AscendingComparator();

        @Override
        public int compare(ExtensionMeta o1, ExtensionMeta o2) {
            return o1.getOrder() - o2.getOrder();
        }
    }
}
