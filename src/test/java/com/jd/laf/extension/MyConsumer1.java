package com.jd.laf.extension;

@Extension("myConsumer1")
public class MyConsumer1 implements Consumer,Ordered {

    @Override
    public int order() {
        return 0;
    }
}
