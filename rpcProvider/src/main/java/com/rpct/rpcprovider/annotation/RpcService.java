package com.rpct.rpcprovider.annotation;


import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)

@Target(ElementType.TYPE)

// 实现了类型的实例容器注入
@Component

public @interface RpcService {

    Class<?> serviceInterface() default Object.class;

    String serviceVersion() default "1.0";
}
