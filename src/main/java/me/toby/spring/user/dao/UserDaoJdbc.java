package me.toby.spring.user.dao;

import java.util.Map;
import me.toby.spring.user.domain.Level;
import me.toby.spring.user.domain.User;
import me.toby.spring.user.service.sqlService.SqlService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDaoJdbc implements UserDao{

    private JdbcTemplate jdbcTemplate;

    /** XML 설정방식 */
    private Map<String, String> sqlMap;

    /** SQL용 객체를 만들어서 DI 하는 방식 */
    private SqlService sqlService;

    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    public void setSqlService(SqlService sqlService) {
        this.sqlService = sqlService;
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
        this.jdbcTemplate.update(//sqlMap.get("add")
            sqlService.getSql("userAdd")
        ,user.getId(), user.getName()
        ,user.getPassword(), user.getEmail(), user.getLevel().intValue()
        ,user.getLogin(), user.getRecommend());
    }

    @Override
    public User get(String id) {
        return this.jdbcTemplate
            .queryForObject(this.sqlService.getSql("userGet"), new Object[]{id}, this.userMapper);
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
