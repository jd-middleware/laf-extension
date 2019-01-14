package com.jd.laf.extension.springboot;

import com.jd.laf.extension.service.Consumer;
import com.jd.laf.extension.service.MyConsumer1;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Consumer1AutoConfiguration {

    @Bean
    public Consumer myConsumer1() {
        return new MyConsumer1();
    }
}
