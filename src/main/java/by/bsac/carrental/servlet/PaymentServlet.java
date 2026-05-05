package by.bsac.carrental.servlet;

import by.bsac.carrental.model.Damage;
import by.bsac.carrental.model.Order;
import by.bsac.carrental.service.DamageService;
import by.bsac.carrental.service.OrderService;
import by.bsac.carrental.service.PaymentService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Сервлет оплаты заказа или счёта за ремонт.
 *
 * GET  /client/payment?orderId=X         — оплата аренды
 * GET  /client/payment?orderId=X&type=REPAIR — оплата ремонта
 * POST /client/payment                   — обработка оплаты (заглушка)
 */
@WebServlet("/client/payment")
public class PaymentServlet extends HttpServlet {

    private final PaymentService paymentService = new PaymentService();
    private final OrderService orderService = new OrderService();
    private final DamageService damageService = new DamageService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            String type = req.getParameter("type");
            if (type == null) type = "RENT";

            Order order = orderService.getOrder(orderId);
            if (order == null) {
                resp.sendRedirect(req.getContextPath() + "/client/orders");
                return;
            }

            BigDecimal amount = order.getTotalPrice();
            if ("REPAIR".equals(type)) {
                Damage damage = damageService.getByOrderId(orderId);
                if (damage == null) {
                    resp.sendRedirect(req.getContextPath() + "/client/orders");
                    return;
                }
                amount = damage.getRepairCost();
                req.setAttribute("damage", damage);
            }

            req.setAttribute("order", order);
            req.setAttribute("amount", amount);
            req.setAttribute("paymentType", type);
            req.getRequestDispatcher("/WEB-INF/views/client/payment.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int orderId      = Integer.parseInt(req.getParameter("orderId"));
            String cardNumber = req.getParameter("cardNumber");
            String type      = req.getParameter("paymentType");
            BigDecimal amount = new BigDecimal(req.getParameter("amount"));

            boolean success = paymentService.processPayment(orderId, amount, cardNumber, type);

            if (!success) {
                req.setAttribute("error", "Ошибка оплаты: проверьте номер карты (16 цифр).");
                Order order = orderService.getOrder(orderId);
                req.setAttribute("order", order);
                req.setAttribute("amount", amount);
                req.setAttribute("paymentType", type);
                req.getRequestDispatcher("/WEB-INF/views/client/payment.jsp").forward(req, resp);
                return;
            }

            // При оплате аренды — переводим заказ в PAID, авто в RENTED
            if ("RENT".equals(type)) {
                orderService.markPaid(orderId);
            } else if ("REPAIR".equals(type)) {
                Damage d = damageService.getByOrderId(orderId);
                if (d != null) {
                    damageService.markPaid(d.getId());
                }
            }

            req.setAttribute("paymentType", type);
            req.getRequestDispatcher("/WEB-INF/views/client/payment_success.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
