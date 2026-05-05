package by.bsac.carrental.service;

import by.bsac.carrental.dao.CarDAO;
import by.bsac.carrental.dao.OrderDAO;
import by.bsac.carrental.model.Car;
import by.bsac.carrental.model.Order;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Сервис заказов: создание, оплата, отклонение, возврат.
 */
public class OrderService {

    private final OrderDAO orderDAO = new OrderDAO();
    private final CarDAO carDAO = new CarDAO();

    /**
     * Создание заказа клиентом.
     * Возвращает созданный заказ или null, если автомобиль не найден/недоступен.
     */
    public Order createOrder(int userId, int carId,
                             String passportSeries, String passportNumber,
                             String passportIssuedBy,
                             LocalDate rentFrom, LocalDate rentTo) throws SQLException {

        Car car = carDAO.findById(carId);
        if (car == null || !"AVAILABLE".equals(car.getStatus())) {
            return null;
        }
        if (rentTo.isBefore(rentFrom) || rentTo.equals(rentFrom)) {
            return null;
        }

        long days = ChronoUnit.DAYS.between(rentFrom, rentTo);
        BigDecimal total = car.getPricePerDay().multiply(BigDecimal.valueOf(days));

        Order order = new Order();
        order.setUserId(userId);
        order.setCarId(carId);
        order.setPassportSeries(passportSeries);
        order.setPassportNumber(passportNumber);
        order.setPassportIssuedBy(passportIssuedBy);
        order.setRentFrom(Date.valueOf(rentFrom));
        order.setRentTo(Date.valueOf(rentTo));
        order.setTotalPrice(total);
        order.setStatus("PENDING");

        orderDAO.save(order);
        return order;
    }

    /**
     * Оплата заказа: меняем статус заказа на PAID, авто на RENTED.
     */
    public void markPaid(int orderId) throws SQLException {
        Order order = orderDAO.findById(orderId);
        if (order == null) return;

        orderDAO.updateStatus(orderId, "PAID");
        carDAO.updateStatus(order.getCarId(), "RENTED");
    }

    /**
     * Администратор отклоняет заявку.
     */
    public void rejectOrder(int orderId, String reason) throws SQLException {
        orderDAO.reject(orderId, reason);
    }

    /**
     * Регистрация возврата без повреждений.
     */
    public void completeOrder(int orderId) throws SQLException {
        Order order = orderDAO.findById(orderId);
        if (order == null) return;

        orderDAO.updateStatus(orderId, "COMPLETED");
        carDAO.updateStatus(order.getCarId(), "AVAILABLE");
    }

    /**
     * Возврат с повреждениями: статус DAMAGED, авто отправляется в REPAIR.
     */
    public void returnDamaged(int orderId) throws SQLException {
        Order order = orderDAO.findById(orderId);
        if (order == null) return;

        orderDAO.updateStatus(orderId, "DAMAGED");
        carDAO.updateStatus(order.getCarId(), "REPAIR");
    }

    public Order getOrder(int id) throws SQLException {
        return orderDAO.findById(id);
    }

    public List<Order> getOrdersByUser(int userId) throws SQLException {
        return orderDAO.findByUserId(userId);
    }

    public List<Order> getAllOrders() throws SQLException {
        return orderDAO.findAll();
    }

    public List<Order> getOrdersByStatus(String status) throws SQLException {
        return orderDAO.findByStatus(status);
    }
}
