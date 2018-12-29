package com.jd.laf.extension;

/**
 * 实例化接口
 */
public interface Instance {

    /**
     * 构建实例
     *
     * @param name 实例名称
     * @param <T>
     * @return
     */
    <T, M> T newInstance(Name<T, M> name);

    class ClazzInstance implements Instance {

        public static final Instance INSTANCE = new ClazzInstance();

        @Override
        public <T, M> T newInstance(final Name<T, M> name) {
            try {
                return name == null ? null : name.getClazz().newInstance();
            } catch (InstantiationException e) {
                return null;
            } catch (IllegalAccessException e) {
                return null;
            }
        }
    }

}
