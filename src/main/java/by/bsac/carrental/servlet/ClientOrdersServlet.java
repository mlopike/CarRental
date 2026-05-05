package by.bsac.carrental.servlet;

import by.bsac.carrental.dao.DamageDAO;
import by.bsac.carrental.model.Damage;
import by.bsac.carrental.model.Order;
import by.bsac.carrental.model.User;
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
 * Список заказов авторизованного клиента.
 */
@WebServlet("/client/orders")
public class ClientOrdersServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();
    private final DamageDAO damageDAO = new DamageDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("user");
        try {
            List<Order> orders = orderService.getOrdersByUser(user.getId());

            // Подгружаем повреждения по заказам со статусом DAMAGED
            Map<Integer, Damage> damages = new HashMap<>();
            for (Order o : orders) {
                if ("DAMAGED".equals(o.getStatus())) {
                    Damage d = damageDAO.findByOrderId(o.getId());
                    if (d != null) damages.put(o.getId(), d);
                }
            }
            req.setAttribute("orders", orders);
            req.setAttribute("damages", damages);
            req.getRequestDispatcher("/WEB-INF/views/client/orders.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
