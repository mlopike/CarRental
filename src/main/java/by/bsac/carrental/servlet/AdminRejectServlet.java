package by.bsac.carrental.servlet;

import by.bsac.carrental.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Отклонение заявки администратором с указанием причины.
 *
 * POST /admin/reject?orderId=X
 */
@WebServlet("/admin/reject")
public class AdminRejectServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            String reason = req.getParameter("reason");
            if (reason == null || reason.trim().isEmpty()) {
                reason = "Причина не указана";
            }
            orderService.rejectOrder(orderId, reason.trim());
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
