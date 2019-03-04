package com.jd.laf.extension;

import java.util.concurrent.ConcurrentMap;

/**
 * Map工具类
 */
public abstract class Maps {

    /**
     * 不存在的时候创建
     *
     * @param map
     * @param key
     * @param function
     * @param <M>
     * @param <T>
     * @return
     */
    public static <M, T> T computeIfAbsent(final ConcurrentMap<M, T> map, final M key, final Function<M, T> function) {
        T result = map.get(key);
        if (result == null) {
            result = function.apply(key);
            T exists = map.putIfAbsent(key, result);
            if (exists != null) {
                result = exists;
            }
        }
        return result;
    }

    /**
     * 函数
     */
    interface Function<M, T> {

        /**
         * Applies this function to the given argument.
         *
         * @param t the function argument
         * @return the function result
         */
        T apply(M t);

    }
}
