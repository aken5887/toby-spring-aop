package me.toby.spring.user.service;

import static me.toby.spring.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static me.toby.spring.user.service.UserServiceImpl.MIN_RECOOMEND_FOR_GOLD;
import static org.junit.Assert.fail;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javafx.application.Platform;
import me.toby.spring.user.domain.Level;
import me.toby.spring.user.domain.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/java/**/test-applicationContext.xml")
public class TransactionManagerTest {

  @Autowired
  UserService userService;
  @Autowired
  PlatformTransactionManager transactionManager;
  List<User> users;

  @Before
  public void setup(){
    users = Arrays.asList(
        new User("bumjin", "박범진", "p1", "user1@ksug.org", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0),
        new User("joytouch", "강명성", "p2", "user2@ksug.org", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
        new User("erwins", "신승한", "p3", "user3@ksug.org", Level.SILVER, 60, MIN_RECOOMEND_FOR_GOLD -1),
        new User("madnite1", "이상호", "p4", "user4@ksug.org", Level.SILVER, 60, MIN_RECOOMEND_FOR_GOLD),
        new User("green", "오민규", "p5", "user5@ksug.org", Level.GOLD, 100, Integer.MAX_VALUE)
    );
  }

  @Test
  public void transactionSync(){

    DefaultTransactionDefinition definition = new DefaultTransactionDefinition();

    Assert.assertTrue(this.userService instanceof java.lang.reflect.Proxy);

    userService.deleteAll();
    Assert.assertEquals(userService.getAll().size(), 0);

    TransactionStatus status = transactionManager.getTransaction(definition);
    //    definition.setReadOnly(true);

    userService.add(users.get(0));
    userService.add(users.get(3));
    Assert.assertEquals(userService.getAll().size(), 2);

    transactionManager.rollback(status);

    Assert.assertEquals(userService.getAll().size(),0);
  }

  @Test//(expected = TransientDataAccessResourceException.class)
  @Transactional//(readOnly = true)
//  @Rollback(false)
  public void transactionAnnoSync(){
    userService.deleteAll();
    userService.add(users.get(1));
    userService.add(users.get(4));
  }
}
