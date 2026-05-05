package by.bsac.carrental.dao;

import by.bsac.carrental.model.User;
import by.bsac.carrental.util.DBConnection;

import java.sql.*;

/**
 * DAO для работы с пользователями.
 */
public class UserDAO {

    /**
     * Поиск по логину/паролю (для авторизации).
     */
    public User findByLoginAndPassword(String login, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE login = ? AND password = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Поиск по логину (для проверки уникальности при регистрации).
     */
    public User findByLogin(String login) throws SQLException {
        String sql = "SELECT * FROM users WHERE login = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Сохранить нового пользователя (регистрация).
     */
    public void save(User user) throws SQLException {
        String sql = "INSERT INTO users (login, password, full_name, email, phone, role) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getRole());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setLogin(rs.getString("login"));
        u.setPassword(rs.getString("password"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setRole(rs.getString("role"));
        // SQLite: created_at — TEXT, парсим из строки
        String ts = rs.getString("created_at");
        if (ts != null && !ts.isEmpty()) {
            try {
                u.setCreatedAt(Timestamp.valueOf(ts.length() == 19 ? ts : ts.substring(0, 19)));
            } catch (IllegalArgumentException ignored) { }
        }
        return u;
    }
}
