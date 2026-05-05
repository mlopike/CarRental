package by.bsac.carrental.model;

import java.sql.Timestamp;

/**
 * Модель пользователя (клиент или администратор).
 */
public class User {
    private int id;
    private String login;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String role;       // CLIENT, ADMIN
    private Timestamp createdAt;

    public User() {}

    public User(String login, String password, String fullName,
                String email, String phone, String role) {
        this.login = login;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
