package me.toby.spring.common.jdk.proxy;

import java.lang.reflect.Proxy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/**/aop-applicationContext.xml")
public class SimpleAopTest {

  @Autowired
  private ISimpleAop myAop;
  
  @Test
  public void testSimpleAop(){
    this.myAop.printMessage();
    Assert.assertTrue(myAop instanceof java.lang.reflect.Proxy);
  }
}
