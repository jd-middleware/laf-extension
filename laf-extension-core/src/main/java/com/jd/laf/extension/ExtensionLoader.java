package com.jd.laf.extension;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 扩展点加载器
 */
public interface ExtensionLoader {

    /**
     * 加载扩展点
     *
     * @param extensible 可扩展的接口
     * @return 扩展点列表
     */
    <T> Collection<Plugin<T>> load(Class<T> extensible);

    /**
     * 包装器
     */
    class Wrapper implements ExtensionLoader {
        protected Collection<ExtensionLoader> loaders = new ConcurrentLinkedQueue<ExtensionLoader>();

        public Wrapper(ExtensionLoader... loaders) {
            if (loaders != null) {
                for (ExtensionLoader loader : loaders) {
                    if (loader instanceof Wrapper) {
                        this.loaders.addAll(((Wrapper) loader).loaders);
                    } else {
                        this.loaders.add(loader);
                    }
                }
            }
        }

        @Override
        public <T> Collection<Plugin<T>> load(final Class<T> extensible) {

            List<Plugin<T>> result = new LinkedList<Plugin<T>>();

            if (loaders != null) {
                for (ExtensionLoader loader : loaders) {
                    Collection<Plugin<T>> plugins = loader.load(extensible);
                    if (plugins != null) {
                        result.addAll(plugins);
                    }
                }
            }

            return result;
        }
    }

}
