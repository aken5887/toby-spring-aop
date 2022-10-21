package me.toby.spring.common.jdk.proxy;

public class HelloTarget implements Hello{
  @Override
  public String sayHello(String name) {
    return "Hello, "+name;
  }

  @Override
  public String sayHi(String name) {
    return "Hi, "+name;
  }

  @Override
  public String sayThankyou(String name) {
    return "Thank you, "+name;
  }
}
