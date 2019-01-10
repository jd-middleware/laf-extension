package com.jd.laf.extension.springboot.starter;

import com.jd.laf.extension.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Spring加载器
 */
public class SpringLoader implements ExtensionLoader, PriorityOrdered, ApplicationContextAware {

    protected ApplicationContext context;

    protected Instantiation instance;

    @Override
    public <T> Collection<Plugin<T>> load(final Class<T> extensible) {
        if (extensible == null) {
            return null;
        }
        List<Plugin<T>> result = new LinkedList<Plugin<T>>();
        String[] names = context.getBeanNamesForType(extensible);
        if (names != null) {
            for (String name : names) {
                T plugin = context.getBean(name, extensible);
                result.add(new Plugin<T>(new Name(plugin.getClass(), name), instance, context.isSingleton(name), plugin));
            }
        }
        return result;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        this.context = context;
        this.instance = new Instantiation() {
            @Override
            public <T, M> T newInstance(final Name<T, M> name) {
                try {
                    return context.getBean(name.getName().toString(), name.getClazz());
                } catch (BeansException e) {
                    return null;
                }
            }
        };
        ExtensionManager.wrap(this);
    }
}
