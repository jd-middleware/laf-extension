package com.jd.laf.extension.service;

import com.jd.laf.extension.Extension;
import com.jd.laf.extension.Ordered;

@Extension("myConsumer")
public class MyConsumer implements Consumer {

    @Override
    public int order() {
        return Ordered.ORDER;
    }
}
