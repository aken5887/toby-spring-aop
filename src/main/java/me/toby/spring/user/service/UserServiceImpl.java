package me.toby.spring.user.service;

import java.util.List;
import me.toby.spring.user.dao.UserDao;
import me.toby.spring.user.domain.Level;
import me.toby.spring.user.domain.User;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.annotation.Transactional;

public class UserServiceImpl implements UserService{

  public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
  public static final int MIN_RECOOMEND_FOR_GOLD = 30;

  private UserDao userDao;
  private MailSender mailSender;

  public void setUserDao(UserDao userDao){
    this.userDao = userDao;
  }

  public void setMailSender(MailSender mailSender){
    this.mailSender = mailSender;
  }

  @Override
  public void add(User user) {
    if(user.getLevel() == null){
      user.setLevel(Level.BASIC);
    }
    userDao.add(user);
  }

  @Override
  public void upgradeLevels() {
    List<User> users = userDao.getAll();
    users.stream()
          .filter(user -> canUpgradeLevel(user))
          .forEach(user -> upgradeLevel(user));
  }

  @Override
  public User get(String id) {
    User user = userDao.get(id);
    return user;
  }

  @Override
  public List<User> getAll() {
    return userDao.getAll();
  }

  @Override
  public void deleteAll() {
    userDao.deleteAll();
  }

  @Override
  public void update(User user) {
    userDao.update(user);
  }

  private boolean canUpgradeLevel(User user){
    Level currentLevel = user.getLevel();
    switch(currentLevel){
      case BASIC:
        return user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER;
      case SILVER:
        return user.getRecommend() >= MIN_RECOOMEND_FOR_GOLD;
      case GOLD:
        return false;
      default:
        throw new IllegalArgumentException("Unknown Level : "+ currentLevel);
    }
  }

  public void upgradeLevel(User user) {
    user.upgradeLevel();
    userDao.update(user);
    sendUpgradeEmail(user);
  }

  private void sendUpgradeEmail(User user) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(user.getEmail());
    mailMessage.setFrom("useradmin@test.kr");
    mailMessage.setSubject("Upgrade ??????");
    mailMessage.setText("??????????????? ???????????????."+user.getLevel());
    this.mailSender.send(mailMessage);
  }

  static class TestUserServiceException extends RuntimeException {
  }

  public static class TestUserService extends UserServiceImpl {
    private String id = "madnite1";

    @Override
    public void upgradeLevel(User user) {
      if(user.getId().equals(this.id)){
        throw new TestUserServiceException();
      }
      super.upgradeLevel(user);
    }

    @Override
    public List<User> getAll() {
      super.getAll().stream().forEach((user) -> {
        super.update(user);
      });
      return null;
    }
  }
}

