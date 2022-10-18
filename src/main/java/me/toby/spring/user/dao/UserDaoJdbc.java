package me.toby.spring.user.dao;

import me.toby.spring.user.domain.Level;
import me.toby.spring.user.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDaoJdbc implements UserDao{

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private RowMapper<User> userMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            user.setLevel(Level.valueOf(rs.getInt("level")));
            user.setLogin(rs.getInt("login"));
            user.setRecommend(rs.getInt("recommend"));
            return user;
        }
    };

    @Override
    public void add(User user) {
        this.jdbcTemplate.update("insert into users (id, name, password, email, level, login, recommend)" +
                        "values(?,?,?,?,?,?,?)"
        ,user.getId(), user.getName()
        ,user.getPassword(), user.getEmail(), user.getLevel().intValue()
        ,user.getLogin(), user.getRecommend());
    }

    @Override
    public User get(String id) {
        return this.jdbcTemplate
                .queryForObject("select * from users where id = ?"
                        , this.userMapper
                        , new Object[]{id});
    }

    @Override
    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id", this.userMapper);
    }

    @Override
    public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
    }

    @Override
    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
    }

    @Override
    public void update(User user) {
        this.jdbcTemplate.update(
                "update users set name = ?, password = ?, email = ?, level = ?, login = ?, " +
                        "recommend = ? where id = ? ", user.getName(), user.getPassword(), user.getEmail(),
                user.getLevel().intValue(), user.getLogin(), user.getRecommend(),
                user.getId());
    }
}
