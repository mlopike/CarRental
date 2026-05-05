package by.bsac.carrental.service;

import by.bsac.carrental.dao.UserDAO;
import by.bsac.carrental.model.User;

import java.sql.SQLException;

/**
 * Сервис для работы с пользователями (авторизация, регистрация).
 */
public class UserService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Авторизация: возвращает User, если найден, иначе null.
     */
    public User authenticate(String login, String password) throws SQLException {
        if (login == null || password == null ||
            login.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }
        return userDAO.findByLoginAndPassword(login.trim(), password);
    }

    /**
     * Регистрация нового клиента.
     * Возвращает true в случае успеха.
     */
    public boolean register(User user) throws SQLException {
        if (userDAO.findByLogin(user.getLogin()) != null) {
            return false; // login уже занят
        }
        user.setRole("CLIENT");
        userDAO.save(user);
        return true;
    }
}
