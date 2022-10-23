package me.toby.spring.user.dao;

import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import me.toby.spring.TestApplicationContext;
import me.toby.spring.user.domain.Level;
import me.toby.spring.user.domain.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestApplicationContext.class)
public class UserDaoJdbcTest {

   @Autowired
   UserDao userDao;

   @Autowired
   DataSource dataSource;

   private List<User> users;

   @Before
   public void setup(){
      users = Arrays.asList(
      new User("gyumee", "구미", "springno1", "user1@ksug.org", Level.BASIC, 1, 0),
      new User("leegw700", "이가원", "springno2", "user2@ksug.org", Level.SILVER, 55, 10)
      );
   }

   @Test
   public void andAndGet(){
      userDao.deleteAll();
      Assert.assertEquals(userDao.getCount(), 0);
   }

   @Test
   @Transactional
   public void addTest(){
      userDao.deleteAll();
      int initalCount = userDao.getCount();
      users.stream().forEach(user -> {
         userDao.add(user);
      });
      Assert.assertEquals(userDao.getCount(), initalCount + users.size());
   }

   @Test
   public void getUser(){
      userDao.deleteAll();
      Assert.assertEquals(userDao.getCount(), 0);
      users.stream().forEach(user -> {
         userDao.add(user);
      });
      User user = userDao.get("gyumee");
      Assert.assertNotNull(user);
   }

   @Test
   public void testPath(){
      System.out.println(UserDaoJdbc.class.getPackage().getName());
   }
}