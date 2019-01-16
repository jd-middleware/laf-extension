package com.jd.laf.extension;

import com.jd.laf.extension.service.Producer;
import com.jd.laf.extension.springboot.ProducerAutoConfiguration;
import com.jd.laf.extension.spring.boot.SpringLoaderAutoConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringLoaderAutoConfiguration.class, ProducerAutoConfiguration.class})
public class ProducerTest {

    @Test
    public void testPlugin() {
        ExtensionPoint<Producer, String> procedure = new ExtensionPointLazy<Producer, String>(Producer.class);
        Assert.assertNotNull(procedure.get());
    }
}
