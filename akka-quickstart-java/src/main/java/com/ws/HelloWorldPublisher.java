package com.ws;

import javax.xml.ws.Endpoint;

public class HelloWorldPublisher {
    public static void main(String[] args) {

        Endpoint.publish("http://localhost:9000/ws/hello", new HelloWorldImpl());
    }
}
