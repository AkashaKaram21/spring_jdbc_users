package com.example.accesdades.ra2.ac1.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.accesdades.ra2.ac1.model.User;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private static final class UsuariRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
            user.setDescription(rs.getString("description"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setUltimAcces(rs.getTimestamp("ultimAcces"));
            user.setDataCreated(rs.getTimestamp("dataCreated"));
            user.setDataUpdated(rs.getTimestamp("dataUpdated"));
            return user;
        }
    }
    
    public int save(User user){
        String sql = "INSERT INTO users (name, description, email, password, ultimAcces, dataCreated, dataUpdated) VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        return jdbcTemplate.update(sql, user.getName(), user.getDescription(), user.getEmail(), user.getPassword(), user.getUltimAcces());
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UsuariRowMapper());
    }

    public List<User> findUserById(long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.query(sql, new UsuariRowMapper(), id);
    }
    
    public int update(Long id, User usuari) {
        String sql = "UPDATE users SET name = ?, description = ?, email = ?, password = ?, ultimAcces = ?, dataUpdated = NOW() WHERE id = ?";
        return jdbcTemplate.update(sql,
                usuari.getName(),
                usuari.getDescription(),
                usuari.getEmail(),
                usuari.getPassword(),
                usuari.getUltimAcces(),
                id);
    }

    public int patch(long id, String newName){
        String sql = "UPDATE users SET name = ?, dataUpdated = NOW() WHERE id = ?";
        return jdbcTemplate.update(sql, newName, id);
    }

    public int delete(Long id){
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}