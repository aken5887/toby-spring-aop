package me.toby.spring.learningtest.factorybean;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/java/**/*-applicationContext.xml")
public class MessageFactoryBeanTest {
  @Autowired
  ApplicationContext applicationContext;

  @Test
  public void getMessageFromFactoryBean(){
    // getObject()라는 메ㅗ스닥 생성해주는 오브젝트가 실제 빈의 오브젝트로 대치
    Object message = applicationContext.getBean("message");
    Assert.assertEquals(message.getClass(), Message.class);
    Assert.assertEquals(((Message)message).getText(), "Factory Bean");
  }

  @Test
  public void getFactoryBean() throws Exception{
    // factoryBean 자체를 return 함
    Object factory = applicationContext.getBean("&message");
    Assert.assertEquals(factory.getClass(), MessageFactoryBean.class);
  }
}