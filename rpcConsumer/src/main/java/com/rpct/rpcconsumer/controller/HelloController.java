package com.rpct.rpcconsumer.controller;


import com.rpcT.facade.HelloFacade;
import com.rpct.rpcconsumer.annotation.RpcReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// RestController 是 ResponseBody 和 Controller 的合体
// 没有 ResponseBody，String 类型是到对应字符串的地址
@RestController
public class HelloController {


    // 加上这个 suppressWarnings 的注解是因为由于可能要到运行时才能发现注入的实例，不注解可能会出错
    @SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection", "SpringJavaAutowiredFieldsWarningInspection"})
    @RpcReference(serviceVersion = "1.0.0", timeout = 3000)
    private HelloFacade helloFacade;

    //@RequestMapping(value = "", method = RequestMethod.GET)
    @GetMapping(value = "/hello")
    public String sayHello() {
        // 函数名字是随便起的
        return helloFacade.hello("rpcT");
    }

}
