package me.toby.spring.user.service;

import java.util.List;
import me.toby.spring.user.domain.User;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserService {
  void add(User user);
  void upgradeLevels();
  @Transactional(readOnly = true)
  User get(String id);
  @Transactional(readOnly = true)
  List<User> getAll();
  void deleteAll();
  void update(User user);
}
