package com.jd.laf.extension.springboot.starter;

import com.jd.laf.extension.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.List;

/**
 * Spring加载器
 */
public class SpringLoader extends SpiLoader implements PriorityOrdered, ApplicationContextAware {

    protected ApplicationContext context;

    protected Instance instance;

    @Override
    protected <T, M> boolean loadExtensions(final Class<T> clazz,
                                            final Name extensibleName,
                                            final List<ExtensionMeta<T, M>> extensionMetas,
                                            final Classify<T, M> classify) {
        boolean result = super.loadExtensions(clazz, extensibleName, extensionMetas, classify);
        int last = !extensionMetas.isEmpty() ? extensionMetas.get(0).getOrder() : com.jd.laf.extension.Ordered.ORDER;
        String[] names = context.getBeanNamesForType(clazz);
        int count = 0;
        ExtensionMeta meta;
        T bean;
        for (String name : names) {
            bean = context.getBean(name, clazz);
            meta = build(bean, extensibleName, classify, instance);
            meta.setSingleton(context.isSingleton(name));
            meta.setName(new Name(bean.getClass(), name));
            extensionMetas.add(meta);
            if (count++ > 0 && meta.getOrder() != last) {
                //顺序不一样，需要排序
                result = true;
            }
            last = meta.getOrder();
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
        this.instance = new Instance() {
            @Override
            public <T, M> T newInstance(final Name<T, M> name) {
                try {
                    return context.getBean(name.getName().toString(), name.getClazz());
                } catch (BeansException e) {
                    return null;
                }
            }
        };
        ExtensionManager.INSTANCE = new ExtensionManager(this);
    }
}
