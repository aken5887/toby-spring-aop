package me.toby.spring.user.service.sqlService;

import java.sql.SQLException;

public interface SqlService {
  String getSql(String key) throws SqlRetrievalFailureException;
}
