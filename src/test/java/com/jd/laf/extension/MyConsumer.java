package com.jd.laf.extension;

@Extension("myConsumer")
public class MyConsumer implements Consumer {

    @Override
    public int order() {
        return Ordered.ORDER;
    }
}
