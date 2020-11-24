package com.ws;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceContext;

@WebService(endpointInterface = "com.ws.HelloWorld")
public class HelloWorldImpl implements HelloWorld {

    @Resource
    private WebServiceContext ctx;

    @Override
    public String sayHello(String name) {

        return "Hello World JAX-WS " + name;
    }
}
