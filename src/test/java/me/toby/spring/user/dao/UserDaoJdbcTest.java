package me.toby.spring.user.dao;

import javax.sql.DataSource;
import me.toby.spring.user.domain.Level;
import me.toby.spring.user.domain.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "file:src/main/java/test-applicationContext.xml")
public class UserDaoJdbcTest {

   @Autowired
   UserDao userDao;

   @Autowired
   DataSource dataSource;

   private User user1;
   private User user2;

   @Before
   public void setup(){
      this.user1 = new User("gyumee", "구미", "springno1", "user1@ksug.org", Level.BASIC, 1, 0);
      this.user2 = new User("leegw700", "이가원", "springno2", "user2@ksug.org", Level.SILVER, 55, 10);
   }

   @Test
   public void andAndGet(){
      userDao.deleteAll();
      Assert.assertEquals(userDao.getCount(), 0);
   }
}