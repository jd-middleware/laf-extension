package com.jd.laf.extension;

import com.jd.laf.extension.Filter.FilterType;
import com.jd.laf.extension.Selector.CacheSelector;
import com.jd.laf.extension.Selector.ListSelector;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static com.jd.laf.extension.Filter.FilterType.CONSUMER;
import static com.jd.laf.extension.Filter.FilterType.PROCEDURE;

public class ExtensionManagerTest {

    @Test
    public void testGetOrLoadSpi() {
        ExtensionPoint spi1 = ExtensionManager.getOrLoadExtensionPoint(Consumer.class);
        ExtensionPoint spi2 = ExtensionManager.getOrLoadExtensionPoint(Consumer.class);
        Assert.assertNotNull(spi1);
        Assert.assertEquals(spi1, spi2);
    }

    @Test
    public void testGetOrLoad() {
        Consumer c1 = ExtensionManager.getOrLoadExtension(Consumer.class);
        Consumer c2 = ExtensionManager.getOrLoadExtension(Consumer.class);
        Assert.assertNotNull(c1);
        Assert.assertEquals(c1, c2);
    }

    @Test
    public void testGet() {
        ExtensionManager.getOrLoadExtensionPoint(Consumer.class);
        Consumer c1 = ExtensionManager.getExtension(Consumer.class, "myConsumer");
        Consumer c2 = ExtensionManager.getExtension(Consumer.class, "myConsumer");
        Consumer c3 = ExtensionManager.getExtension("consumer", "myConsumer");
        Assert.assertNotNull(c1);
        Assert.assertEquals(c1, c2);
        Assert.assertEquals(c1, c3);
    }

    @Test
    public void testSelector() {
        ExtensionPoint spi1 = ExtensionManager.getOrLoadExtensionPoint(Consumer.class);
        ExtensionSelector<Consumer, String, Integer, Consumer> selector = new ExtensionSelector(spi1, new Selector.MatchSelector<Consumer, String, Integer>() {
            @Override
            protected boolean match(Consumer target, Integer condition) {
                return target.order() == condition;
            }
        });
        Consumer c1 = ExtensionManager.getExtension(Consumer.class, "myConsumer");
        Assert.assertEquals(c1, selector.select(Ordered.ORDER));
    }

    @Test
    public void testPrototype() {
        Producer producer1 = ExtensionManager.getOrLoadExtension(Producer.class, "com.jd.laf.extension.MyProducer");
        Assert.assertNotNull(producer1);
        Producer producer2 = ExtensionManager.getOrLoadExtension(Producer.class, "com.jd.laf.extension.MyProducer");
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

    @Test
    public void testListSelector() {
        ExtensionSelector<Filter, String, FilterType, List<Filter>> selector = new ExtensionSelector<Filter, String, FilterType, List<Filter>>(
                new ExtensionPointLazy<Filter, String>(Filter.class),
                new CacheSelector<Filter, String, FilterType, List<Filter>>(
                        new ListSelector<Filter, String, FilterType>() {
                            @Override
                            protected boolean match(Filter target, FilterType condition) {
                                return condition == CONSUMER ? target.isConsumer() : !target.isConsumer();
                            }
                        }));
        List<Filter> consumers = selector.select(CONSUMER);
        List<Filter> procedures = selector.select(PROCEDURE);
        Assert.assertEquals(consumers.size(), 2);
        Assert.assertEquals(procedures.size(), 1);
    }
}
