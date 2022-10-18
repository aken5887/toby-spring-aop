package me.toby.spring.user.service;

import java.lang.reflect.Proxy;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

public class TxProxyFactoryBean implements FactoryBean<Object> {
  Object target;
  PlatformTransactionManager transactionManager;
  String pattern;
  Class<?> serviceInterface;

  public void setTarget(Object target){
    this.target = target;
  }

  public void setTransactionManager(
      PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public void setServiceInterface(Class<?> serviceInterface) {
    this.serviceInterface = serviceInterface;
  }

  // FactoryBean 인터페이스 구현 메소드
  @Override
  public Object getObject() throws Exception {
    TransactionHandler transactionHandler = new TransactionHandler();
    transactionHandler.setPattern(pattern);
    transactionHandler.setTarget(target);
    transactionHandler.setTransactionManager(transactionManager);

    return Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class[]{serviceInterface},
                    transactionHandler);
  }

  @Override
  public Class<?> getObjectType() {
    return this.serviceInterface;
  }

  @Override
  public boolean isSingleton() {
    return false;
  }
}
