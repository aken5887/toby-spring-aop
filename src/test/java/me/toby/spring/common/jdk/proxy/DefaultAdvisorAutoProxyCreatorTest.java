package me.toby.spring.common.jdk.proxy;

import me.toby.spring.common.jdk.proxy.DynamicProxyTest.UppercaseAdvice;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

public class DefaultAdvisorAutoProxyCreatorTest {

  @Test
  public void classNamePointcutAdvisor() {
    // 포인트 컷
    NameMatchMethodPointcut classMethodPointcut
        = new NameMatchMethodPointcut(){
      @Override
      public ClassFilter getClassFilter() {
        return new ClassFilter() {
          @Override
          public boolean matches(Class<?> clazz) {
            return clazz.getSimpleName().startsWith("HelloT");
          }
        };
      }
    };
    classMethodPointcut.setMappedName("sayH*");

    // 테스트
    class HelloWorld extends HelloTarget{}
    checkAdviced(new HelloWorld(), classMethodPointcut, false);
    checkAdviced(new HelloTarget(), classMethodPointcut, true);
    class HelloToby extends HelloTarget {}
    checkAdviced(new HelloToby(), classMethodPointcut, true);
  }

  /**
   * advisor 적용 여부 확인
   * @param target
   * @param pointcut
   * @param adviced
   */
  private void checkAdviced(Object target, NameMatchMethodPointcut pointcut, boolean adviced) {
    ProxyFactoryBean pfBean = new ProxyFactoryBean();
    pfBean.setTarget(target);
    pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

    Hello proxiedHello = (Hello) pfBean.getObject();

    if(adviced){
      Assert.assertEquals(proxiedHello.sayHello("yong"), "HELLO, YONG");
      Assert.assertEquals(proxiedHello.sayThankyou("yong"), "Thank you, yong");
    }else{
      Assert.assertEquals(proxiedHello.sayHello("yong"), "Hello, yong");
      Assert.assertEquals(proxiedHello.sayThankyou("yong"), "Thank you, yong");
    }
  }
}
