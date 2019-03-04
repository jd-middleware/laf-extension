package com.jd.laf.extension;

@Extension(value = "myConsumer",provider = "test")
public class MyConsumer implements Consumer {

    @Override
    public int order() {
        return Ordered.ORDER;
    }
}
