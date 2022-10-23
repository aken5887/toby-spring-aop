package me.toby.spring;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import me.toby.spring.user.dao.UserDao;
import me.toby.spring.user.dao.UserDaoJdbc;
import me.toby.spring.user.service.DummyMailSender;
import me.toby.spring.user.service.UserService;
import me.toby.spring.user.service.UserServiceImpl;
import me.toby.spring.user.service.UserServiceImpl.TestUserService;
import me.toby.spring.user.service.sqlService.SqlService;
import me.toby.spring.user.service.sqlService.UserSqlServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
//@ImportResource("file:src/main/java/test-applicationContext.xml")
@EnableTransactionManagement
public class TestApplicationContext {

  private Properties properties;

  public TestApplicationContext() {
    this.properties = new Properties();
    ClassPathResource resource = new ClassPathResource("database.properties");

    try(InputStream is = resource.getInputStream()) {
      this.properties.load(is);
    }catch(IOException io){
      throw new RuntimeException("file not found");
    }
  }

  @Bean
  public DataSource dataSource() throws ClassNotFoundException {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    dataSource.setDriverClass((Class<? extends Driver>) Class.forName(properties.getProperty("jdbc.driver")));
    dataSource.setUrl(properties.getProperty("jdbc.url"));
    dataSource.setUsername(properties.getProperty("jdbc.name"));
    dataSource.setPassword(properties.getProperty("jdbc.password"));
    return dataSource;
  }

  @Bean
  public PlatformTransactionManager transactionManager() throws ClassNotFoundException {
    DataSourceTransactionManager tm = new DataSourceTransactionManager();
    tm.setDataSource(dataSource());
    return tm;
  }

  @Bean
  public UserDao userDao() throws ClassNotFoundException {
    UserDaoJdbc userDao = new UserDaoJdbc();
    userDao.setDataSource(dataSource());
    userDao.setSqlService(sqlService());
    return userDao;
  }

  @Bean
  public SqlService sqlService() {
    UserSqlServiceImpl userSqlService = new UserSqlServiceImpl();
    Map<String, String> sqlMap = new HashMap<>();
    sqlMap.put("userAdd", "insert into users (id, name, password, email, level, login, recommend) values(?,?,?,?,?,?,?)");
    sqlMap.put("userGet", "select * from users where id = ?");
    userSqlService.setSqlMap(sqlMap);
    return userSqlService;
  }

  @Bean
  public MailSender mailSender(){
    return new DummyMailSender();
  }

  @Bean
  public UserService userService() throws ClassNotFoundException {
    UserServiceImpl userService = new UserServiceImpl();
    userService.setUserDao(userDao());
    userService.setMailSender(mailSender());
    return userService;
  }

  @Bean
  public UserService testUserService() throws ClassNotFoundException {
    TestUserService userService = new TestUserService();
    userService.setMailSender(mailSender());
    userService.setUserDao(userDao());
    return userService;
  }
}
