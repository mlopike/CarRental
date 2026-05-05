package by.bsac.carrental.servlet;

import by.bsac.carrental.model.Car;
import by.bsac.carrental.service.CarService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Управление автопарком (просмотр, добавление, удаление).
 */
@WebServlet("/admin/cars")
public class AdminCarsServlet extends HttpServlet {

    private final CarService carService = new CarService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Car> cars = carService.getAllCars();
            req.setAttribute("cars", cars);
            req.getRequestDispatcher("/WEB-INF/views/admin/cars.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String action = req.getParameter("action");
            if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                carService.deleteCar(id);
            } else if ("add".equals(action)) {
                Car car = new Car();
                car.setBrand(req.getParameter("brand"));
                car.setModel(req.getParameter("model"));
                car.setYear(Integer.parseInt(req.getParameter("year")));
                car.setLicensePlate(req.getParameter("licensePlate"));
                car.setColor(req.getParameter("color"));
                car.setTransmission(req.getParameter("transmission"));
                car.setFuelType(req.getParameter("fuelType"));
                car.setPricePerDay(new BigDecimal(req.getParameter("pricePerDay")));
                car.setImageUrl(req.getParameter("imageUrl"));
                car.setDescription(req.getParameter("description"));
                car.setStatus("AVAILABLE");
                carService.addCar(car);
            } else if ("changeStatus".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                String status = req.getParameter("status");
                carService.updateStatus(id, status);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/cars");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
