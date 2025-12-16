package com.example.accesdades.ra2.ac1.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.accesdades.ra2.ac1.model.User;
import com.example.accesdades.ra2.ac1.logging.CustomLogging; 
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private CustomLogging customLogging; 

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
            user.setImage_path(rs.getString("image_path")); 
            return user;
        }
    }

    public int save(User user){
        customLogging.logInfo(
            "UserRepository", 
            "save", 
            "Executant INSERT: INSERT INTO users (name, description, email, password, ultimAcces, image_path, dataCreated, dataUpdated) VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())"
        );
        
        try {
            String sql = "INSERT INTO users (name, description, email, password, ultimAcces, image_path, dataCreated, dataUpdated) VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())";
            int result = jdbcTemplate.update(sql, 
                    user.getName(), 
                    user.getDescription(), 
                    user.getEmail(), 
                    user.getPassword(), 
                    user.getUltimAcces(), 
                    user.getImage_path());
            
            customLogging.logInfo(
                "UserRepository", 
                "save", 
                "INSERT executat correctament. Usuari creat amb ID: " + (user.getId() != null ? user.getId() : "nou")
            );
            
            return result;
        } catch (Exception e) {
            customLogging.logError(
                "UserRepository", 
                "save", 
                "Error executant INSERT a la taula users", 
                e
            );
            throw e;
        }
    }

    public List<User> findAll() {
        customLogging.logInfo(
            "UserRepository", 
            "findAll", 
            "Executant consulta: SELECT * FROM users"
        );
        
        try {
            String sql = "SELECT * FROM users";
            List<User> users = jdbcTemplate.query(sql, new UsuariRowMapper());
            
            customLogging.logInfo(
                "UserRepository", 
                "findAll", 
                "Consulta SELECT * FROM users executada correctament. S'han trobat " + users.size() + " usuaris"
            );
            
            return users;
        } catch (Exception e) {
            customLogging.logError(
                "UserRepository", 
                "findAll", 
                "Error executant consulta SELECT * FROM users", 
                e
            );
            throw e;
        }
    }

    public List<User> findUserById(long id) {
        customLogging.logInfo(
            "UserRepository", 
            "findUserById", 
            "Executant consulta: SELECT * FROM users WHERE id = " + id
        );
        
        try {
            String sql = "SELECT * FROM users WHERE id = ?";
            List<User> users = jdbcTemplate.query(sql, new UsuariRowMapper(), id);
            
            customLogging.logInfo(
                "UserRepository", 
                "findUserById", 
                "Consulta SELECT * FROM users WHERE id = " + id + " executada correctament. S'han trobat " + users.size() + " usuaris"
            );
            
            return users;
        } catch (Exception e) {
            customLogging.logError(
                "UserRepository", 
                "findUserById", 
                "Error executant consulta SELECT * FROM users WHERE id = " + id, 
                e
            );
            throw e;
        }
    }

    public int update(Long id, User user) {
        customLogging.logInfo(
            "UserRepository", 
            "update", 
            "Executant UPDATE: UPDATE users SET name = '" + user.getName() + "', description = '" + user.getDescription() + "', email = '" + user.getEmail() + "' WHERE id = " + id
        );
        
        try {
            String sql = "UPDATE users SET name = ?, description = ?, email = ?, password = ?, ultimAcces = ?, image_path = ?, dataUpdated = NOW() WHERE id = ?";
            int result = jdbcTemplate.update(sql,
                    user.getName(),
                    user.getDescription(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getUltimAcces(),
                    user.getImage_path(), 
                    id);
            
            customLogging.logInfo(
                "UserRepository", 
                "update", 
                "UPDATE executat correctament per l'usuari amb ID: " + id + ". Files afectades: " + result
            );
            
            return result;
        } catch (Exception e) {
            customLogging.logError(
                "UserRepository", 
                "update", 
                "Error executant UPDATE a la taula users per l'ID: " + id, 
                e
            );
            throw e;
        }
    }

    public int patch(long id, String newName){
        customLogging.logInfo(
            "UserRepository", 
            "patch", 
            "Executant PATCH: UPDATE users SET name = '" + newName + "' WHERE id = " + id
        );
        
        try {
            String sql = "UPDATE users SET name = ?, dataUpdated = NOW() WHERE id = ?";
            int result = jdbcTemplate.update(sql, newName, id);
            
            customLogging.logInfo(
                "UserRepository", 
                "patch", 
                "PATCH executat correctament per l'usuari amb ID: " + id + ". Nom canviat a: " + newName + ". Files afectades: " + result
            );
            
            return result;
        } catch (Exception e) {
            customLogging.logError(
                "UserRepository", 
                "patch", 
                "Error executant PATCH a la taula users per l'ID: " + id, 
                e
            );
            throw e;
        }
    }

    public int delete(Long id){
        customLogging.logInfo(
            "UserRepository", 
            "delete", 
            "Executant DELETE: DELETE FROM users WHERE id = " + id
        );
        
        try {
            String sql = "DELETE FROM users WHERE id = ?";
            int result = jdbcTemplate.update(sql, id);
            
            customLogging.logInfo(
                "UserRepository", 
                "delete", 
                "DELETE executat correctament per l'usuari amb ID: " + id + ". Files afectades: " + result
            );
            
            return result;
        } catch (Exception e) {
            customLogging.logError(
                "UserRepository", 
                "delete", 
                "Error executant DELETE a la taula users per l'ID: " + id, 
                e
            );
            throw e;
        }
    }

    public int updateImagePath(long id, String imagePath){
        customLogging.logInfo(
            "UserRepository", 
            "updateImagePath", 
            "Executant UPDATE image_path: UPDATE users SET image_path = '" + imagePath + "' WHERE id = " + id
        );
        
        try {
            String sql = "UPDATE users SET image_path = ?, dataUpdated = NOW() WHERE id = ?"; 
            int result = jdbcTemplate.update(sql, imagePath, id);
            
            customLogging.logInfo(
                "UserRepository", 
                "updateImagePath", 
                "UPDATE image_path executat correctament per l'usuari amb ID: " + id + ". Nou path: " + imagePath + ". Files afectades: " + result
            );
            
            return result;
        } catch (Exception e) {
            customLogging.logError(
                "UserRepository", 
                "updateImagePath", 
                "Error executant UPDATE image_path a la taula users per l'ID: " + id, 
                e
            );
            throw e;
        }
    }
}