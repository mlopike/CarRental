package by.bsac.carrental.servlet;

import by.bsac.carrental.dao.DamageDAO;
import by.bsac.carrental.model.Damage;
import by.bsac.carrental.model.Order;
import by.bsac.carrental.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Список всех заказов для администратора с фильтром по статусу.
 *
 * GET /admin/orders?status=PAID — фильтр (опционально)
 */
@WebServlet("/admin/orders")
public class AdminOrdersServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();
    private final DamageDAO damageDAO = new DamageDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String status = req.getParameter("status");
        try {
            List<Order> orders;
            if (status == null || status.trim().isEmpty() || "ALL".equals(status)) {
                orders = orderService.getAllOrders();
            } else {
                orders = orderService.getOrdersByStatus(status);
            }
            Map<Integer, Damage> damages = new HashMap<>();
            for (Order o : orders) {
                if ("DAMAGED".equals(o.getStatus())) {
                    Damage d = damageDAO.findByOrderId(o.getId());
                    if (d != null) damages.put(o.getId(), d);
                }
            }
            req.setAttribute("orders", orders);
            req.setAttribute("damages", damages);
            req.setAttribute("currentStatus", status == null ? "ALL" : status);
            req.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
