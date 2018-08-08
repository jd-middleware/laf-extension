package com.jd.laf.extension;

import org.junit.Assert;
import org.junit.Test;

public class UrlTest {

    @Test
    public void test() {
        URL url = URL.valueOf("http://${user.name}:${user.password}@xxxxx/sadfsfasf");
        System.out.println(url.toString(true, true));
        url = URL.valueOf("http://${xxxx:user.name}:${user.password}@xxxxx/sadfsfasf");
        System.out.println(url.getUser());
        url = URL.valueOf("file:/D:\\config");
        Assert.assertEquals(url.getPath(), "D:\\config");
        Assert.assertEquals(url.getAbsolutePath(), "D:\\config");
    }
}
