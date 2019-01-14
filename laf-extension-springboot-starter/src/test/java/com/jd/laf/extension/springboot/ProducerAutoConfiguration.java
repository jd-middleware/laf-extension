package com.jd.laf.extension.springboot;

import com.jd.laf.extension.MyProducer;
import com.jd.laf.extension.Producer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(Producer.class)
public class ProducerAutoConfiguration {

    @Bean
    public Producer myProducer() {
        return new MyProducer();
    }
}
