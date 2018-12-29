package com.jd.laf.extension;

import org.junit.Assert;
import org.junit.Test;

public class ExtensionManagerTest {

    @Test
    public void test() {
        ExtensionPoint spi1 = ExtensionManager.getOrLoadSpi(Consumer.class);
        ExtensionPoint spi2 = ExtensionManager.getOrLoadSpi(Consumer.class);
        ExtensionPoint spi3 = ExtensionManager.getSpi(Consumer.class);
        ExtensionPoint spi4 = ExtensionManager.getOrLoadSpi(Producer.class);
        Consumer c1 = ExtensionManager.getOrLoad(Consumer.class);
        Assert.assertNotNull(c1);
        Assert.assertEquals(spi1, spi2);
        Assert.assertEquals(spi1, spi3);
        Consumer consumer1 = ExtensionManager.get(Consumer.class, "myConsumer");
        Assert.assertNotNull(consumer1);
        Consumer consumer2 = ExtensionManager.get(Consumer.class, "myConsumer");
        Assert.assertEquals(consumer1, consumer2);
        Assert.assertNotEquals(consumer1, c1);

        ExtensionSelector<Consumer, String, Integer, Consumer> selector = new ExtensionSelector(spi1, new Selector.MatchSelector<Consumer, String, Integer>() {
            @Override
            protected boolean match(Consumer target, Integer condition) {
                return target.order() == condition;
            }
        });

        Assert.assertEquals(consumer1, selector.select(Ordered.ORDER));

        Producer producer1 = ExtensionManager.get(Producer.class, "com.jd.laf.extension.MyProducer");
        Assert.assertNotNull(producer1);
        Producer producer2 = ExtensionManager.get(Producer.class, "com.jd.laf.extension.MyProducer");
        Assert.assertNotEquals(producer1, producer2);
        Producer producer3 = ExtensionManager.get(Producer.class.getName(), "com.jd.laf.extension.MyProducer");
        Assert.assertNotNull(producer3);
    }
}
