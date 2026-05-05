package by.bsac.carrental.dao;

import by.bsac.carrental.model.Payment;
import by.bsac.carrental.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для работы с платежами.
 */
public class PaymentDAO {

    public void save(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (order_id, amount, payment_type, card_number, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, payment.getOrderId());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setString(3, payment.getPaymentType());
            ps.setString(4, payment.getCardNumber());
            ps.setString(5, payment.getStatus() != null ? payment.getStatus() : "SUCCESS");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    payment.setId(keys.getInt(1));
                }
            }
        }
    }

    public List<Payment> findByOrderId(int orderId) throws SQLException {
        String sql = "SELECT * FROM payments WHERE order_id = ? ORDER BY payment_date DESC";
        List<Payment> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getInt("id"));
        p.setOrderId(rs.getInt("order_id"));
        p.setAmount(rs.getBigDecimal("amount"));
        p.setPaymentType(rs.getString("payment_type"));
        p.setCardNumber(rs.getString("card_number"));
        // SQLite: timestamp — TEXT
        String ts = rs.getString("payment_date");
        if (ts != null && !ts.isEmpty()) {
            try {
                p.setPaymentDate(Timestamp.valueOf(ts.length() == 19 ? ts : ts.substring(0, 19)));
            } catch (IllegalArgumentException ignored) { }
        }
        p.setStatus(rs.getString("status"));
        return p;
    }
}
