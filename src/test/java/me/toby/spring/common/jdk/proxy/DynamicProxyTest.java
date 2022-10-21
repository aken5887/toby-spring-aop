package me.toby.spring.common.jdk.proxy;

import java.lang.reflect.Proxy;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

public class DynamicProxyTest {

  /**
   * JDK 다이나믹 프록시 생성
   */
  @Test
  public void simpleProxy(){
    String name = "yong";

    Hello proxiedHello = (Hello) Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class[]{Hello.class},
        new UppercaseHandler(new HelloTarget(), "sayH"));

    Assert.assertEquals(proxiedHello.sayHello(name), "HELLO, YONG");
    Assert.assertEquals(proxiedHello.sayThankyou(name), "Thank you, yong");
  }

  @Test
  public void proxyFactoryBean(){
    String name = "yong";

    ProxyFactoryBean pfBean = new ProxyFactoryBean();
    pfBean.setTarget(new HelloTarget()); // 타깃설정
//    pfBean.addAdvice(new UppercaseAdvice());
    NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
    pointcut.setMappedName("sayH*");
    pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

    Hello proxiedHello = (Hello) pfBean.getObject();
    System.out.println(proxiedHello.getClass());
    Assert.assertEquals(proxiedHello.sayHello(name), "HELLO, YONG");
    Assert.assertEquals(proxiedHello.sayThankyou(name), "Thank you, yong");
  }

  /**
   * Advice : 타깃 오브젝트에 종속되지 않는 순수한 부가기능을 담은 오브젝트
   */
  static class UppercaseAdvice implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
      // 인자로 target이 필요하지 않음
      // MethodInvocation은 메소드 정보와 함께 타깃의 정보 또한 알고있음
      String returnValue = (String) invocation.proceed();
      return returnValue.toUpperCase(); // 부가기능 적용
    }
  }
}
