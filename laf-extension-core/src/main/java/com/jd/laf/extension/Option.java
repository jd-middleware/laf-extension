package com.jd.laf.extension;

/**
 * 选项
 *
 * @param <T>
 */
public class Option<T> {
    //值
    T value;

    public Option() {
    }

    public Option(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
