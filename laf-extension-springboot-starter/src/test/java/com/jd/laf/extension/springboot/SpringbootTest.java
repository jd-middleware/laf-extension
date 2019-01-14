package com.jd.laf.extension.springboot;

import com.jd.laf.extension.ExtensionPoint;
import com.jd.laf.extension.ExtensionPointLazy;
import com.jd.laf.extension.Producer;
import com.jd.laf.extension.springboot.starter.SpringLoaderAutoConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringLoaderAutoConfiguration.class, ProducerAutoConfiguration.class})
public class SpringbootTest {

    @Test
    public void testPlugin1() {
        ExtensionPoint<Producer, String> procedure = new ExtensionPointLazy<Producer, String>(Producer.class);
        Assert.assertNotNull(procedure.get());
    }

    @Test
    public void testPlugin2() {
        ExtensionPoint<Producer, String> procedure = new ExtensionPointLazy<Producer, String>(Producer.class);
        Assert.assertNotNull(procedure.get());
    }
}
