package by.bsac.carrental.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Модель заказа на аренду автомобиля.
 */
public class Order {
    private int id;
    private int userId;
    private int carId;
    private String passportSeries;
    private String passportNumber;
    private String passportIssuedBy;
    private Date rentFrom;
    private Date rentTo;
    private BigDecimal totalPrice;
    private String status;
    // PENDING, PAID, REJECTED, COMPLETED, DAMAGED
    private String rejectionReason;
    private Timestamp createdAt;

    // Поля, подгружаемые JOIN'ом
    private String userFullName;
    private Car car;

    public Order() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCarId() { return carId; }
    public void setCarId(int carId) { this.carId = carId; }

    public String getPassportSeries() { return passportSeries; }
    public void setPassportSeries(String passportSeries) { this.passportSeries = passportSeries; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    public String getPassportIssuedBy() { return passportIssuedBy; }
    public void setPassportIssuedBy(String passportIssuedBy) { this.passportIssuedBy = passportIssuedBy; }

    public Date getRentFrom() { return rentFrom; }
    public void setRentFrom(Date rentFrom) { this.rentFrom = rentFrom; }

    public Date getRentTo() { return rentTo; }
    public void setRentTo(Date rentTo) { this.rentTo = rentTo; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }

    /**
     * Текстовое представление статуса (для UI).
     */
    public String getStatusLabel() {
        switch (status) {
            case "PENDING":   return "Ожидает оплаты";
            case "PAID":      return "Оплачен (активен)";
            case "REJECTED":  return "Отклонён";
            case "COMPLETED": return "Завершён";
            case "DAMAGED":   return "Возвращён с повреждениями";
            default:          return status;
        }
    }
}
