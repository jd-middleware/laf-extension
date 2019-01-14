package com.jd.laf.extension.springboot;

import com.jd.laf.extension.service.Consumer;
import com.jd.laf.extension.service.MyConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerAutoConfiguration {

    @Bean
    public Consumer myConsumer() {
        return new MyConsumer();
    }
}
