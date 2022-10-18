package me.toby.spring.user.service;

import me.toby.spring.user.domain.User;

public interface UserService {
  void add(User user);
  void upgradeLevels();
}
