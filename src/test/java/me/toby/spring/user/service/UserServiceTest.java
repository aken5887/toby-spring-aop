package me.toby.spring.user.service;

import static me.toby.spring.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static me.toby.spring.user.service.UserServiceImpl.MIN_RECOOMEND_FOR_GOLD;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.toby.spring.TestApplicationContext;
import me.toby.spring.user.dao.UserDao;
import me.toby.spring.user.domain.Level;
import me.toby.spring.user.domain.User;
import me.toby.spring.user.service.UserServiceImpl.TestUserServiceException;
import me.toby.spring.user.service.factorybean.TxProxyFactoryBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestApplicationContext.class)
public class UserServiceTest {

  @Autowired
  private UserService userService;
  @Autowired
  private UserDao userDao;
  @Autowired
  private MailSender mailSender;
  @Autowired
  private PlatformTransactionManager transactionManager;
  @Autowired
  private ApplicationContext applicationContext;
  @Autowired
  private UserService testUserService;

  List<User> users;

  @Before
  public void setup(){
    users = Arrays.asList(
        new User("bumjin", "?????????", "p1", "user1@ksug.org", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0),
        new User("joytouch", "?????????", "p2", "user2@ksug.org", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
        new User("erwins", "?????????", "p3", "user3@ksug.org", Level.SILVER, 60, MIN_RECOOMEND_FOR_GOLD -1),
        new User("madnite1", "?????????", "p4", "user4@ksug.org", Level.SILVER, 60, MIN_RECOOMEND_FOR_GOLD),
        new User("green", "?????????", "p5", "user5@ksug.org", Level.GOLD, 100, Integer.MAX_VALUE)
    );
  }

  @Test
  public void upgradeLevels() {
    UserServiceImpl userServiceImpl = new UserServiceImpl();
    MockUserDao mockUserDao = new MockUserDao(this.users);
    userServiceImpl.setUserDao(mockUserDao);

    MockMailSender mockMailSender = new MockMailSender();
    userServiceImpl.setMailSender(mockMailSender);

    userServiceImpl.upgradeLevels();

    List<User> updated = mockUserDao.getUpdated();
    Assert.assertEquals(updated.size(), 2);

    List<String> requests = mockMailSender.getRequests();
    Assert.assertEquals(requests.size(), 2);
    Assert.assertEquals(requests.get(0), users.get(1).getEmail());
    Assert.assertEquals(requests.get(1), users.get(3).getEmail());
  }

  @Test
  public void mockUpgradeLevels() {
    UserServiceImpl userServiceImpl = new UserServiceImpl();

    UserDao mockUserDao = mock(UserDao.class);
    when(mockUserDao.getAll()).thenReturn(this.users);
    userServiceImpl.setUserDao(mockUserDao);

    MailSender mockMailSender = mock(MailSender.class);
    userServiceImpl.setMailSender(mockMailSender);

    userServiceImpl.upgradeLevels();
    verify(mockUserDao, times(2)).update(any(User.class));
    verify(mockUserDao).update(users.get(1));
    Assert.assertEquals(users.get(1).getLevel(), Level.SILVER);
    verify(mockUserDao).update(users.get(3));
    Assert.assertEquals(users.get(3).getLevel(), Level.GOLD);

    ArgumentCaptor<SimpleMailMessage> mailMessageArg
        =ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(mockMailSender, times(2)).send(mailMessageArg.capture());
    List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
    Assert.assertEquals(mailMessages.get(0).getTo()[0], users.get(1).getEmail());
    Assert.assertEquals(mailMessages.get(1).getTo()[0], users.get(3).getEmail());
  }

  static class MockUserDao implements UserDao{
    private List<User> users;
    private List<User> updated = new ArrayList<>();

    private MockUserDao(List<User> users){
      this.users = users;
    }

    public List<User> getUpdated(){
      return this.updated;
    }

    @Override
    public List<User> getAll(){
      return this.users;
    }

    @Override
    public void update(User user) {
      this.updated.add(user);
    }

    @Override
    public User get(String id) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getCount() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void add(User user) {
      throw new UnsupportedOperationException();
    }
  }

  static class MockMailSender implements MailSender {
    private List<String> requests = new ArrayList<>();

    public List<String> getRequests(){
      return this.requests;
    }

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
      this.requests.add(simpleMessage.getTo()[0]);
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {
    }
  }

  private void checkLevelUpgrade(User user, boolean upgraded){
    User userUpdated = userDao.get(user.getId());
    if(upgraded){
      Assert.assertEquals(userUpdated.getLevel(), user.getLevel().nextLevel());
    }else{
      Assert.assertEquals(userUpdated.getLevel(), user.getLevel());
    }
  }

  @Test
  public void add() {
    userDao.deleteAll();

    User userWithLevel = users.get(4);
    User userWithoutLevel = users.get(0);
    userWithoutLevel.setLevel(null);

    userService.add(userWithLevel);
    userService.add(userWithoutLevel);

    User userWithLevelRead = userDao.get(userWithLevel.getId());
    User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

    Assert.assertEquals(userWithLevelRead.getLevel(), userWithLevel.getLevel());
    Assert.assertEquals(userWithoutLevelRead.getLevel(), Level.BASIC);
  }

  @Test
  @DirtiesContext
  public void upgradeOrNothing() throws Exception {
//      TestUserService testUserService = new TestUserService(users.get(3).getId());
//      testUserService.setUserDao(userDao);
//      testUserService.setMailSender(mailSender);

      Class<TxProxyFactoryBean> txProxyFactoryBeanClass = TxProxyFactoryBean.class;
      Class<ProxyFactoryBean> proxyFactoryBeanClass = ProxyFactoryBean.class;

      /** ProxyFactoryBean - getObject() */
      ProxyFactoryBean txProxyFactoryBean
          = applicationContext.getBean("&userService", proxyFactoryBeanClass);
      txProxyFactoryBean.setTarget(testUserService);

      UserService txUserService = (UserService) txProxyFactoryBean.getObject();

      userDao.deleteAll();
      for(User user:users){
        userDao.add(user);
      }

      try{
        txUserService.upgradeLevels();
        fail("TestUserServiceException expected");
      }catch(TestUserServiceException e){
      }

      checkLevelUpgrade(users.get(1), false);
  }

  /**
   * DefaultAdvisorAutoProxy test
   */
  @Test
  public void upgradeAllOrNothing(){
    userDao.deleteAll();
    for(User user:users){
      userDao.add(user);
    }
    try{
      System.out.println(this.testUserService.getClass());
      this.testUserService.upgradeLevels();
      fail("TestUserServiceException expected");
    }catch (TestUserServiceException e){
    }

    checkLevelUpgrade(users.get(1), false);
  }

  @Test
  public void advisorAutoProxyCreator(){
    Assert.assertTrue(this.testUserService instanceof java.lang.reflect.Proxy);
    Assert.assertTrue(this.userService instanceof java.lang.reflect.Proxy);
  }

  @Test(expected = TransientDataAccessException.class)
  public void readOnlyTransactionAttribute(){
    Assert.assertTrue(this.testUserService instanceof java.lang.reflect.Proxy);
    this.testUserService.getAll();
  }

}