package com.jd.laf.extension;

public interface Filter {

    boolean isConsumer();

    enum FilterType {
        PROCEDURE,
        CONSUMER
    }
}
