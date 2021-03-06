package com.rpct.rpcconsumer.core;

import com.rpcT.rpcCore.RpcConstants;
import com.rpcT.rpcCore.RpcServiceHelper;
import com.rpct.rpcconsumer.annotation.RpcReference;
import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@Component
@Slf4j
public class RpcConsumerPostProcessor implements ApplicationContextAware, BeanClassLoaderAware, BeanFactoryPostProcessor {

    // RpcReference 是在 controller 的 facade 上注解的

    private ApplicationContext context;

    private ClassLoader classLoader;

    private final Map<String, BeanDefinition> rpcRefBeanDefinitions =  new LinkedHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null) {
                Class<?> clz = ClassUtils.resolveClassName(beanClassName, this.classLoader);
                ReflectionUtils.doWithFields(clz, this::parseRpcReference);//因为我们的 RpcReference 是对属性 field 进行注解，所以要对 clz 带有这个注解的属性进行处理
            }
        }
        // 这个 registry 是 spring 容器的，完成
        // 注意下面的这段不能在 for 循环中，调 bug 找了很长时间
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        this.rpcRefBeanDefinitions.forEach((beanName, beanDefinition1) -> {
            if (context.containsBean(beanName)) {
                throw new IllegalArgumentException("spring context already has a bean named " + beanName);
            }
            registry.registerBeanDefinition(beanName, rpcRefBeanDefinitions.get(beanName)); // value 用 beanDefinition 也可以表示
            log.info("registered RpcReferenceBean {} success.", beanName);
        });
    }

    private void parseRpcReference(Field field) {
        // annotation 对于每个注入的 field 都是不同的
        RpcReference annotation = AnnotationUtils.getAnnotation(field, RpcReference.class);//Utils 用的是 spring 的
        if (annotation != null) {
            // 有 RpcReferenceBean 的所有属性和方法，并设置了对应变量的值
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RpcReferenceBean.class);
            builder.setInitMethodName(RpcConstants.INIT_METHOD_NAME); // 子项的是 RpcReferenceBean 的 init() 方法，我们初始化了代理对象
            // 下面的 name 要和 ReferenceBean 的成员变量的名称一致
            // addPropertyValue 方法首先会找 RpcReferenceBean 有没有对应名称的变量，如果有就会直接进行赋值
            builder.addPropertyValue("interfaceClass", field.getType()); // field 对应被注解的接口
            builder.addPropertyValue("serviceVersion", annotation.serviceVersion()); // 这个会调用RpcReferenceBean 的 set 方法对成员变量进行赋值
            builder.addPropertyValue("registryAddress", annotation.registryAddress());
            builder.addPropertyValue("registryType", annotation.registryType());
            builder.addPropertyValue("timeout", annotation.timeout());

            BeanDefinition beanDefinition = builder.getBeanDefinition();
            rpcRefBeanDefinitions.put(field.getName(), beanDefinition);
        }
    }
}
