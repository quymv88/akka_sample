package com.ws.client;

import com.sun.xml.internal.ws.api.message.Headers;
import com.sun.xml.internal.ws.developer.WSBindingProvider;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class MyClient {

    public static void main(String[] args) throws MalformedURLException {

        /*URL newEndpoint = new URL("http://localhost:4444/ws/hello?wsdl");
        QName qname = new QName("http://ws.com/", "HelloWorldImplService");
        HelloWorldImplService service = new HelloWorldImplService(newEndpoint, qname);*/

        HelloWorldImplService service = new HelloWorldImplService();

        HelloWorld port = service.getHelloWorldImplPort();
        WSBindingProvider wp = (WSBindingProvider) port;
        Map<String, Object> reqContext = wp.getRequestContext();
        reqContext.put(BindingProvider.USERNAME_PROPERTY, "quymv");
        reqContext.put(BindingProvider.PASSWORD_PROPERTY, "P@ssw0rd");
        reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:4444/ws/hello");
        // set to <S:Header><ahihi xmlns="">test-value</ahihi></S:Header>
        // not http header
        wp.setOutboundHeaders(Headers.create(new QName("ahihi"), "test-value"));

        String result = port.sayHello("QuyMV");
        System.out.println(result);

    }
}
