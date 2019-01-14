package com.jd.laf.extension.springboot;

import com.jd.laf.extension.service.MyProducer;
import com.jd.laf.extension.service.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProducerAutoConfiguration {

    @Bean
    public Producer myProducer() {
        return new MyProducer();
    }
}
