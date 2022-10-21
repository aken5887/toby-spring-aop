package me.toby.spring.common.jdk.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class SimpleAdvice implements MethodInterceptor {
  String message;

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    System.out.println("before advice : message = "+this.message);
    Object returnValue = invocation.proceed();
    System.out.println("after advice : message = "+this.message);
    return returnValue;
  }
}
