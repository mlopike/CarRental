package by.bsac.carrental.dao;

import by.bsac.carrental.model.Damage;
import by.bsac.carrental.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для работы с записями о повреждениях (SQLite).
 *
 * SQLite-особенности:
 *  - is_paid хранится как INTEGER (0/1), а не BOOLEAN.
 *  - timestamp хранится как TEXT.
 */
public class DamageDAO {

    public void save(Damage damage) throws SQLException {
        String sql = "INSERT INTO damages (order_id, description, repair_cost, is_paid) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, damage.getOrderId());
            ps.setString(2, damage.getDescription());
            ps.setBigDecimal(3, damage.getRepairCost());
            ps.setInt(4, damage.isPaid() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    damage.setId(keys.getInt(1));
                }
            }
        }
    }

    public Damage findByOrderId(int orderId) throws SQLException {
        String sql = "SELECT * FROM damages WHERE order_id = ? ORDER BY return_date DESC LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Damage> findAll() throws SQLException {
        String sql = "SELECT * FROM damages ORDER BY return_date DESC";
        List<Damage> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public void markPaid(int damageId) throws SQLException {
        String sql = "UPDATE damages SET is_paid = 1 WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, damageId);
            ps.executeUpdate();
        }
    }

    private Damage mapRow(ResultSet rs) throws SQLException {
        Damage d = new Damage();
        d.setId(rs.getInt("id"));
        d.setOrderId(rs.getInt("order_id"));
        d.setDescription(rs.getString("description"));
        d.setRepairCost(rs.getBigDecimal("repair_cost"));
        // SQLite: BOOLEAN → INTEGER 0/1
        d.setPaid(rs.getInt("is_paid") == 1);
        // SQLite: timestamp как TEXT
        String ts = rs.getString("return_date");
        if (ts != null && !ts.isEmpty()) {
            try {
                d.setReturnDate(Timestamp.valueOf(ts.length() == 19 ? ts : ts.substring(0, 19)));
            } catch (IllegalArgumentException ignored) { }
        }
        return d;
    }
}
