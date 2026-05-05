package by.bsac.carrental.dao;

import by.bsac.carrental.model.Car;
import by.bsac.carrental.model.Order;
import by.bsac.carrental.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для работы с заказами (SQLite).
 *
 * SQLite-особенности:
 *  - даты хранятся в TEXT (формат YYYY-MM-DD), читаем через getString и парсим в java.sql.Date.
 *  - timestamp хранится в TEXT (CURRENT_TIMESTAMP даёт строку "YYYY-MM-DD HH:MM:SS").
 */
public class OrderDAO {

    /**
     * Создать новый заказ.
     */
    public void save(Order order) throws SQLException {
        String sql = "INSERT INTO orders (user_id, car_id, passport_series, passport_number, " +
                     "passport_issued_by, rent_from, rent_to, total_price, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getUserId());
            ps.setInt(2, order.getCarId());
            ps.setString(3, order.getPassportSeries());
            ps.setString(4, order.getPassportNumber());
            ps.setString(5, order.getPassportIssuedBy());
            // SQLite: даты как ISO-строка
            ps.setString(6, order.getRentFrom().toString());
            ps.setString(7, order.getRentTo().toString());
            ps.setBigDecimal(8, order.getTotalPrice());
            ps.setString(9, order.getStatus() != null ? order.getStatus() : "PENDING");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    order.setId(keys.getInt(1));
                }
            }
        }
    }

    public List<Order> findByUserId(int userId) throws SQLException {
        String sql = baseSelectSql() + " WHERE o.user_id = ? ORDER BY o.created_at DESC";
        return executeListQuery(sql, userId);
    }

    public List<Order> findAll() throws SQLException {
        String sql = baseSelectSql() + " ORDER BY o.created_at DESC";
        return executeListQuery(sql, null);
    }

    public List<Order> findByStatus(String status) throws SQLException {
        String sql = baseSelectSql() + " WHERE o.status = ? ORDER BY o.created_at DESC";
        List<Order> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public Order findById(int id) throws SQLException {
        String sql = baseSelectSql() + " WHERE o.id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public void updateStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    public void reject(int orderId, String reason) throws SQLException {
        String sql = "UPDATE orders SET status = 'REJECTED', rejection_reason = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reason);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    private String baseSelectSql() {
        return "SELECT o.*, u.full_name AS user_full_name, " +
               "c.brand, c.model, c.year, c.license_plate, c.image_url, c.price_per_day, " +
               "c.status AS car_status " +
               "FROM orders o " +
               "JOIN users u ON o.user_id = u.id " +
               "JOIN cars c ON o.car_id = c.id";
    }

    private List<Order> executeListQuery(String sql, Integer userIdParam) throws SQLException {
        List<Order> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (userIdParam != null) {
                ps.setInt(1, userIdParam);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setUserId(rs.getInt("user_id"));
        o.setCarId(rs.getInt("car_id"));
        o.setPassportSeries(rs.getString("passport_series"));
        o.setPassportNumber(rs.getString("passport_number"));
        o.setPassportIssuedBy(rs.getString("passport_issued_by"));

        // Даты в SQLite — TEXT, парсим из строки YYYY-MM-DD
        String fromStr = rs.getString("rent_from");
        String toStr = rs.getString("rent_to");
        o.setRentFrom(fromStr != null ? Date.valueOf(fromStr) : null);
        o.setRentTo(toStr != null ? Date.valueOf(toStr) : null);

        o.setTotalPrice(rs.getBigDecimal("total_price"));
        o.setStatus(rs.getString("status"));
        o.setRejectionReason(rs.getString("rejection_reason"));

        // Timestamp в SQLite — TEXT
        o.setCreatedAt(parseTimestamp(rs.getString("created_at")));
        o.setUserFullName(rs.getString("user_full_name"));

        Car car = new Car();
        car.setId(rs.getInt("car_id"));
        car.setBrand(rs.getString("brand"));
        car.setModel(rs.getString("model"));
        car.setYear(rs.getInt("year"));
        car.setLicensePlate(rs.getString("license_plate"));
        car.setImageUrl(rs.getString("image_url"));
        car.setPricePerDay(rs.getBigDecimal("price_per_day"));
        car.setStatus(rs.getString("car_status"));
        o.setCar(car);

        return o;
    }

    /**
     * Преобразует строку SQLite-формата (например "2026-05-03 12:34:56") в Timestamp.
     */
    private static Timestamp parseTimestamp(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            // SQLite иногда возвращает с миллисекундами, иногда без
            return Timestamp.valueOf(s.length() == 19 ? s : s.substring(0, 19));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
