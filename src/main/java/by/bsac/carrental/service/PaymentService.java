package by.bsac.carrental.service;

import by.bsac.carrental.dao.PaymentDAO;
import by.bsac.carrental.model.Payment;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Сервис оплаты (заглушка платёжной системы).
 *
 * Эмулирует обращение к платёжному шлюзу:
 *  - валидирует номер карты (формально, по длине);
 *  - всегда возвращает SUCCESS, если карта корректна.
 */
public class PaymentService {

    private final PaymentDAO paymentDAO = new PaymentDAO();

    /**
     * Провести платёж (заглушка).
     */
    public boolean processPayment(int orderId, BigDecimal amount,
                                  String cardNumber, String paymentType) throws SQLException {

        // Удаляем пробелы из номера карты
        String cleanCard = cardNumber == null ? "" : cardNumber.replaceAll("\\s+", "");

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setPaymentType(paymentType);
        // Маскируем все цифры, оставляя только последние 4
        if (cleanCard.length() >= 4) {
            payment.setCardNumber("**** **** **** " + cleanCard.substring(cleanCard.length() - 4));
        } else {
            payment.setCardNumber("INVALID");
        }

        // Простая проверка: 16 цифр
        boolean valid = cleanCard.matches("\\d{16}");
        payment.setStatus(valid ? "SUCCESS" : "FAILED");

        paymentDAO.save(payment);
        return valid;
    }
}
