package com.test.article.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 *  !ssss
 *
 */

public class Server {

    private final static Logger logger = LoggerFactory.getLogger(Server.class);
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath:spring.xml", "classpath:spring-mybatis.xml",
                "classpath:bean-provider.xml", "classpath:bean-consumer.xml");
        context.start();

        logger.info("----------------------dubbo service begin to start------------------------------------");
        try {
            System.in.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}