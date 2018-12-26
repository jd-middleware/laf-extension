package com.jd.laf.extension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 扩展点扫描
 */
public interface ExtensionScanner {

    /**
     * 扫描扩展点
     *
     * @return 扩展点类型
     */
    Set<Class> scan();

    /**
     * 默认扩展点扫描，扫描文件META-INF/com.jd.laf.extension
     */
    class DefaultScanner implements ExtensionScanner {

        @Override
        public Set<Class> scan() {
            Set<Class> classes = new LinkedHashSet<Class>();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try {
                Enumeration<URL> urls = classLoader.getResources("META-INF/com.jd.laf.extension");
                while (urls.hasMoreElements()) {
                    scan(classLoader, urls.nextElement(), classes);
                }
            } catch (IOException e) {
            }
            return classes;
        }

        /**
         * 加载文件中的类
         *
         * @param classLoader
         * @param url
         * @param classes
         */
        protected void scan(final ClassLoader classLoader, final URL url, final Set<Class> classes) {
            BufferedReader input = null;
            try {
                input = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                while ((line = input.readLine()) != null) {
                    classes.add(classLoader.loadClass(line));
                }
            } catch (IOException e) {
            } catch (ClassNotFoundException e) {
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
}
