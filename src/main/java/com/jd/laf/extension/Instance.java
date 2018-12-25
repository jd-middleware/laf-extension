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
    <T> T newInstance(Name name);

    class ClazzInstance implements Instance {

        public static final Instance INSTANCE = new ClazzInstance();

        @Override
        public <T> T newInstance(final Name name) {
            try {
                return name == null ? null : (T) name.getClazz().newInstance();
            } catch (InstantiationException e) {
                return null;
            } catch (IllegalAccessException e) {
                return null;
            }
        }
    }

}
