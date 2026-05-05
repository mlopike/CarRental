package by.bsac.carrental.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Модель платежа.
 */
public class Payment {
    private int id;
    private int orderId;
    private BigDecimal amount;
    private String paymentType; // RENT, REPAIR
    private String cardNumber;
    private Timestamp paymentDate;
    private String status;      // SUCCESS, FAILED

    public Payment() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public Timestamp getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Timestamp paymentDate) { this.paymentDate = paymentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
