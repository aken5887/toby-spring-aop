package me.toby.spring.user.service.proxyFactoryBean;

import java.lang.reflect.InvocationTargetException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 스프링의 어드바이스 interface 구현
 * 어드바이스는 특정 타깃에 의존하지 않고 재사용 가능함
 */
public class TransactionAdvice implements MethodInterceptor {

  PlatformTransactionManager transactionManager;

  public void setTransactionManager(
      PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  /**
   * 부가기능 및 콜백 오브젝트 호출
   * @param invocation 타깃을 호출하는 기능을 가진 콜백 오브젝트를
   *                   프록시로 부터 제공 받음
   * @return
   * @throws Throwable
   */
  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    TransactionStatus status
        = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
    try{
      Object returnValue = invocation.proceed();
      this.transactionManager.commit(status);
      return returnValue;
    }catch(RuntimeException e){
      this.transactionManager.rollback(status);
      throw e;
    }
  }
}
