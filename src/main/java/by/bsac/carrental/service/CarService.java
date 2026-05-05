package by.bsac.carrental.service;

import by.bsac.carrental.dao.CarDAO;
import by.bsac.carrental.model.Car;

import java.sql.SQLException;
import java.util.List;

public class CarService {

    private final CarDAO carDAO = new CarDAO();

    public List<Car> getAvailableCars() throws SQLException {
        return carDAO.findAvailable();
    }

    public List<Car> getAllCars() throws SQLException {
        return carDAO.findAll();
    }

    public Car getCar(int id) throws SQLException {
        return carDAO.findById(id);
    }

    public void addCar(Car car) throws SQLException {
        carDAO.save(car);
    }

    public void updateStatus(int carId, String status) throws SQLException {
        carDAO.updateStatus(carId, status);
    }

    public void deleteCar(int id) throws SQLException {
        carDAO.delete(id);
    }
}
