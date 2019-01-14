package com.jd.laf.extension;

import com.jd.laf.extension.ExtensionPoint;
import com.jd.laf.extension.ExtensionPointLazy;
import com.jd.laf.extension.service.Consumer;
import com.jd.laf.extension.springboot.ConsumerAutoConfiguration;
import com.jd.laf.extension.springboot.starter.SpringLoaderAutoConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringLoaderAutoConfiguration.class, ConsumerAutoConfiguration.class})
public class ConsumerTest {

    @Test
    public void testPlugin() {
        ExtensionPoint<Consumer, String> consumer = new ExtensionPointLazy<Consumer, String>(Consumer.class);
        Assert.assertNotNull(consumer.get());
    }
}
