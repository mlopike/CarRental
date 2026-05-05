package by.bsac.carrental.servlet;

import by.bsac.carrental.service.DamageService;
import by.bsac.carrental.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Регистрация возврата автомобиля администратором.
 *
 * POST /admin/return
 *   - returnType=OK         — возврат без повреждений
 *   - returnType=DAMAGED   — возврат с повреждениями (description, repairCost)
 */
@WebServlet("/admin/return")
public class AdminReturnServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();
    private final DamageService damageService = new DamageService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            String returnType = req.getParameter("returnType");

            if ("DAMAGED".equals(returnType)) {
                String description = req.getParameter("description");
                String costStr = req.getParameter("repairCost");
                if (description == null || description.trim().isEmpty()
                    || costStr == null || costStr.trim().isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + "/admin/orders");
                    return;
                }
                BigDecimal cost = new BigDecimal(costStr);
                damageService.registerDamage(orderId, description.trim(), cost);
            } else {
                orderService.completeOrder(orderId);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
