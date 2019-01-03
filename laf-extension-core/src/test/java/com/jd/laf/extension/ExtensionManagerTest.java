package com.jd.laf.extension;

import org.junit.Assert;
import org.junit.Test;

public class ExtensionManagerTest {

    @Test
    public void testGetOrLoadSpi() {
        ExtensionPoint spi1 = ExtensionManager.getOrLoadSpi(Consumer.class);
        ExtensionPoint spi2 = ExtensionManager.getOrLoadSpi(Consumer.class);
        Assert.assertNotNull(spi1);
        Assert.assertEquals(spi1, spi2);
    }

    @Test
    public void testGetOrLoad() {
        Consumer c1 = ExtensionManager.getOrLoad(Consumer.class);
        Consumer c2 = ExtensionManager.getOrLoad(Consumer.class);
        Assert.assertNotNull(c1);
        Assert.assertEquals(c1, c2);
    }

    @Test
    public void testGet() {
        ExtensionManager.getOrLoadSpi(Consumer.class);
        Consumer c1 = ExtensionManager.get(Consumer.class, "myConsumer");
        Consumer c2 = ExtensionManager.get(Consumer.class, "myConsumer");
        Consumer c3 = ExtensionManager.get("consumer", "myConsumer");
        Assert.assertNotNull(c1);
        Assert.assertEquals(c1, c2);
        Assert.assertEquals(c1, c3);
    }

    @Test
    public void testSelector() {
        ExtensionPoint spi1 = ExtensionManager.getOrLoadSpi(Consumer.class);
        ExtensionSelector<Consumer, String, Integer, Consumer> selector = new ExtensionSelector(spi1, new Selector.MatchSelector<Consumer, String, Integer>() {
            @Override
            protected boolean match(Consumer target, Integer condition) {
                return target.order() == condition;
            }
        });
        Consumer c1 = ExtensionManager.get(Consumer.class, "myConsumer");
        Assert.assertEquals(c1, selector.select(Ordered.ORDER));
    }

    @Test
    public void testPrototype() {
        Producer producer1 = ExtensionManager.getOrLoad(Producer.class, "com.jd.laf.extension.MyProducer");
        Assert.assertNotNull(producer1);
        Producer producer2 = ExtensionManager.getOrLoad(Producer.class, "com.jd.laf.extension.MyProducer");
        Assert.assertNotEquals(producer1, producer2);
    }

    @Test
    public void testLazy() {
        ExtensionPoint<Consumer, String> sp1 = new ExtensionPointLazy(Consumer.class);
        ExtensionPoint<Consumer, String> sp2 = new ExtensionPointLazy(Consumer.class);
        Consumer c1 = sp1.get();
        Consumer c2 = sp2.get();
        Assert.assertNotNull(c1);
        Assert.assertEquals(c1, sp1.get());
        Assert.assertEquals(c1, c2);
    }
}
