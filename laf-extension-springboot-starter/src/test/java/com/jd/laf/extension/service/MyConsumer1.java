package com.jd.laf.extension.service;

import com.jd.laf.extension.Extension;
import com.jd.laf.extension.Ordered;

@Extension("myConsumer1")
public class MyConsumer1 implements Consumer {

    @Override
    public int order() {
        return Ordered.ORDER;
    }
}
