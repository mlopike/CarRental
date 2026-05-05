package by.bsac.carrental.service;

import by.bsac.carrental.dao.DamageDAO;
import by.bsac.carrental.model.Damage;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Сервис для регистрации повреждений и счетов за ремонт.
 */
public class DamageService {

    private final DamageDAO damageDAO = new DamageDAO();
    private final OrderService orderService = new OrderService();

    /**
     * Зарегистрировать повреждение и выставить счёт за ремонт.
     */
    public Damage registerDamage(int orderId, String description, BigDecimal repairCost) throws SQLException {
        Damage damage = new Damage();
        damage.setOrderId(orderId);
        damage.setDescription(description);
        damage.setRepairCost(repairCost);
        damage.setPaid(false);
        damageDAO.save(damage);

        // Ставим заказу статус DAMAGED, авто — в REPAIR
        orderService.returnDamaged(orderId);
        return damage;
    }

    public Damage getByOrderId(int orderId) throws SQLException {
        return damageDAO.findByOrderId(orderId);
    }

    public void markPaid(int damageId) throws SQLException {
        damageDAO.markPaid(damageId);
    }
}
