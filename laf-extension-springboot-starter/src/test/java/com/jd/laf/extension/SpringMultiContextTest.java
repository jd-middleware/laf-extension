package com.jd.laf.extension;

import com.jd.laf.extension.service.Consumer;
import com.jd.laf.extension.springboot.ConsumerAutoConfiguration;
import com.jd.laf.extension.springboot.starter.SpringLoaderAutoConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringLoaderAutoConfiguration.class, ConsumerAutoConfiguration.class})
public class SpringMultiContextTest {

    @Autowired
    ApplicationContext parent;

    @Test
    public void testPlugin() {
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(SpringLoaderAutoConfiguration.class, ConsumerAutoConfiguration.class);
        context.setParent(parent);
        context.start();
        ExtensionPoint<Consumer, String> consumer = new ExtensionPointLazy<Consumer, String>(Consumer.class);
        Assert.assertEquals(consumer.size(), 2);
        Consumer target = consumer.get();
        Assert.assertNotNull(target);
        context.close();
        Assert.assertEquals(consumer.size(), 1);
        target = consumer.get();
        Assert.assertNotNull(target);
    }
}
