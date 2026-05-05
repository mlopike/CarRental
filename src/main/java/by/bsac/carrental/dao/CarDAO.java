package by.bsac.carrental.dao;

import by.bsac.carrental.model.Car;
import by.bsac.carrental.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для работы с автомобилями.
 */
public class CarDAO {

    /**
     * Получить все автомобили.
     */
    public List<Car> findAll() throws SQLException {
        String sql = "SELECT * FROM cars ORDER BY brand, model";
        List<Car> cars = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cars.add(mapRow(rs));
            }
        }
        return cars;
    }

    /**
     * Получить только доступные автомобили (для клиента).
     */
    public List<Car> findAvailable() throws SQLException {
        String sql = "SELECT * FROM cars WHERE status = 'AVAILABLE' ORDER BY brand, model";
        List<Car> cars = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cars.add(mapRow(rs));
            }
        }
        return cars;
    }

    public Car findById(int id) throws SQLException {
        String sql = "SELECT * FROM cars WHERE id = ?";
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

    /**
     * Изменить статус автомобиля.
     */
    public void updateStatus(int carId, String status) throws SQLException {
        String sql = "UPDATE cars SET status = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, carId);
            ps.executeUpdate();
        }
    }

    /**
     * Добавить автомобиль (для администратора).
     */
    public void save(Car car) throws SQLException {
        String sql = "INSERT INTO cars (brand, model, year, license_plate, color, " +
                     "transmission, fuel_type, price_per_day, image_url, description, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, car.getBrand());
            ps.setString(2, car.getModel());
            ps.setInt(3, car.getYear());
            ps.setString(4, car.getLicensePlate());
            ps.setString(5, car.getColor());
            ps.setString(6, car.getTransmission());
            ps.setString(7, car.getFuelType());
            ps.setBigDecimal(8, car.getPricePerDay());
            ps.setString(9, car.getImageUrl());
            ps.setString(10, car.getDescription());
            ps.setString(11, car.getStatus() != null ? car.getStatus() : "AVAILABLE");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    car.setId(keys.getInt(1));
                }
            }
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM cars WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Car mapRow(ResultSet rs) throws SQLException {
        Car c = new Car();
        c.setId(rs.getInt("id"));
        c.setBrand(rs.getString("brand"));
        c.setModel(rs.getString("model"));
        c.setYear(rs.getInt("year"));
        c.setLicensePlate(rs.getString("license_plate"));
        c.setColor(rs.getString("color"));
        c.setTransmission(rs.getString("transmission"));
        c.setFuelType(rs.getString("fuel_type"));
        c.setPricePerDay(rs.getBigDecimal("price_per_day"));
        c.setImageUrl(rs.getString("image_url"));
        c.setDescription(rs.getString("description"));
        c.setStatus(rs.getString("status"));
        return c;
    }
}
