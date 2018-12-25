package com.jd.laf.extension;

import org.junit.Assert;
import org.junit.Test;

public class ExtensionManagerTest {

    @Test
    public void test() {
        ExtensionManager manager = ExtensionManager.INSTANCE;
        ExtensionSpi spi1 = manager.add(Consumer.class);
        ExtensionSpi spi2 = manager.add(Consumer.class);
        ExtensionSpi spi3 = manager.add(Producer.class);
        Assert.assertEquals(spi1, spi2);
        Consumer consumer1 = manager.getExtension(Consumer.class, "myConsumer");
        Assert.assertNotNull(consumer1);
        Consumer consumer2 = manager.getExtension(Consumer.class, "myConsumer");
        Assert.assertEquals(consumer1, consumer2);

        Producer producer1 = manager.getExtension(Producer.class, "com.jd.laf.extension.MyProducer");
        Assert.assertNotNull(producer1);
        Producer producer2 = manager.getExtension(Producer.class, "com.jd.laf.extension.MyProducer");
        Assert.assertNotEquals(producer1, producer2);
        Producer producer3 = manager.getExtension(Producer.class.getName(), "com.jd.laf.extension.MyProducer");
        Assert.assertNotNull(producer3);
    }
}
