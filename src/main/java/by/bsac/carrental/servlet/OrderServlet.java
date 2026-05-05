package by.bsac.carrental.servlet;

import by.bsac.carrental.model.Car;
import by.bsac.carrental.model.Order;
import by.bsac.carrental.model.User;
import by.bsac.carrental.service.CarService;
import by.bsac.carrental.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Создание нового заказа клиентом.
 *
 * GET  /client/order?carId=X — форма заказа
 * POST /client/order        — отправка формы
 */
@WebServlet("/client/order")
public class OrderServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();
    private final CarService carService = new CarService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int carId = Integer.parseInt(req.getParameter("carId"));
            Car car = carService.getCar(carId);
            if (car == null || !"AVAILABLE".equals(car.getStatus())) {
                resp.sendRedirect(req.getContextPath() + "/client/cars");
                return;
            }
            req.setAttribute("car", car);
            req.getRequestDispatcher("/WEB-INF/views/client/order_form.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/client/cars");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        try {
            int carId = Integer.parseInt(req.getParameter("carId"));
            String passportSeries = req.getParameter("passportSeries");
            String passportNumber = req.getParameter("passportNumber");
            String passportIssued = req.getParameter("passportIssuedBy");
            LocalDate from = LocalDate.parse(req.getParameter("rentFrom"));
            LocalDate to   = LocalDate.parse(req.getParameter("rentTo"));

            // Валидация
            if (passportSeries == null || passportSeries.trim().isEmpty()
                || passportNumber == null || passportNumber.trim().isEmpty()
                || from.isAfter(to) || from.equals(to) || from.isBefore(LocalDate.now())) {

                Car car = carService.getCar(carId);
                req.setAttribute("car", car);
                req.setAttribute("error", "Проверьте корректность дат и паспортных данных");
                req.getRequestDispatcher("/WEB-INF/views/client/order_form.jsp").forward(req, resp);
                return;
            }

            Order order = orderService.createOrder(user.getId(), carId,
                                                   passportSeries, passportNumber, passportIssued,
                                                   from, to);
            if (order == null) {
                resp.sendRedirect(req.getContextPath() + "/client/cars");
                return;
            }
            // Перенаправляем на страницу оплаты
            resp.sendRedirect(req.getContextPath() + "/client/payment?orderId=" + order.getId());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
