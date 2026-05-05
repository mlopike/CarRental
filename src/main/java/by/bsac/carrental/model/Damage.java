package by.bsac.carrental.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Модель повреждения автомобиля при возврате.
 */
public class Damage {
    private int id;
    private int orderId;
    private String description;
    private BigDecimal repairCost;
    private boolean isPaid;
    private Timestamp returnDate;

    public Damage() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getRepairCost() { return repairCost; }
    public void setRepairCost(BigDecimal repairCost) { this.repairCost = repairCost; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }

    public Timestamp getReturnDate() { return returnDate; }
    public void setReturnDate(Timestamp returnDate) { this.returnDate = returnDate; }
}
