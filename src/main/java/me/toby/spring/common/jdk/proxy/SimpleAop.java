package me.toby.spring.common.jdk.proxy;

public class SimpleAop implements ISimpleAop{
  String message;

  public void setMessage(String message) {
    this.message = message;
  }

  public void printMessage(){
    System.out.println(this.message);
  }
}