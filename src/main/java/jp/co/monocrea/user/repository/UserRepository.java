package jp.co.monocrea.user.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jp.co.monocrea.user.entity.User;

@ApplicationScoped
public class UserRepository {
    
    DataSource dataSource;

    private static final Logger log = Logger.getLogger(UserRepository.class);

    @Inject
    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Long insertUser(String name) throws SQLException {
        String sql = "INSERT INTO users (name) VALUES (?) RETURNING id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("id");
            } else {
                throw new SQLException("Failed to insert user");
            }
        }
    }

    public List<User> searchUsers(
        Long id,
        String name,
        String sort,
        String order,
        int limit,
        int offset
    ) throws SQLException, IllegalArgumentException {
        
        StringBuilder sql = new StringBuilder("SELECT id, name FROM users WHERE 1=1");
        List<User> users = new ArrayList<>();

        List<Object> parameters = new ArrayList<>();
        
        if (id != null) {
            sql.append(" AND id = ?");
            parameters.add(id);
        }
        if (name != null && !name.isBlank()) {
            sql.append(" AND name ILIKE ?");
            parameters.add("%" + name + "%");
        }
        if (!sort.equals("id") && !sort.equals("name")) {
            throw new IllegalArgumentException("Invalid sort column: " + sort);
        }
        if (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException("Invalid sort order: " + order);
        }
        sql.append(" ORDER BY " + sort + " " + order);

        sql.append(" LIMIT ? OFFSET ?");
        parameters.add(limit);
        parameters.add(offset);
        
        log.infov(sql.toString());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(new User(rs.getLong("id"), rs.getString("name")));
            }
            
            return users;
        }
    }

    public long allUsersCount(
        Long id,
        String name
    ) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT count(*) from users WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        if (id != null) {
            sql.append(" AND id = ?");
            parameters.add(id);
        }
        if (name != null && !name.isBlank()) {
            sql.append(" AND name ILIKE ?");
            parameters.add("%" + name + "%");
        }

        log.infov(sql.toString());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        }
    }

    public boolean updatetUser(User user) throws SQLException {
        String sql = "UPDATE users SET name = ? WHERE id = ?";
        List<Object> parameters = new ArrayList<>();

        if (user.id == null) {
            throw new IllegalArgumentException("Invalid id column: " + user.id);
        }
        if (user.name == null || user.name.isBlank()) {
            throw new IllegalArgumentException("Invalid name column: " + user.name);
        }
        parameters.add(user.name);
        parameters.add(user.id);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean deleteUser(Long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        if (id == null) {
            throw new IllegalArgumentException("Invalid id column: " + id);
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
}
