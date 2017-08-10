package com.test.article.test;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring.xml",
        "classpath:dubbo.xml","classpath:dubbo-consumer.xml"})
public class BaseJunitTest {

    @Before
    public void init(){
        String aa="";
        System.out.println("------------aa----------------");
    }

    @Test
    public void aa(){
        String bb="";
    }
}
