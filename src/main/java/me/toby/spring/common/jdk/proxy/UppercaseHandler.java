package me.toby.spring.common.jdk.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler implements InvocationHandler {

  private Object target;
  private String pattern;

  public UppercaseHandler(Object target, String pattern){
    this.target = target;
    this.pattern = pattern;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if(method.getName().startsWith(pattern)){
      Object returnValue = method.invoke(target, args);
      return returnValue.toString().toUpperCase();
    }
    return method.invoke(target, args);
  }
}
