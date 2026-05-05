package by.bsac.carrental.servlet;

import by.bsac.carrental.model.Car;
import by.bsac.carrental.service.CarService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Список доступных автомобилей для клиента.
 */
@WebServlet("/client/cars")
public class CarListServlet extends HttpServlet {

    private final CarService carService = new CarService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Car> cars = carService.getAvailableCars();
            req.setAttribute("cars", cars);
            req.getRequestDispatcher("/WEB-INF/views/client/cars.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
