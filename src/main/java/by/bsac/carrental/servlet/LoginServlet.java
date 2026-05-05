package by.bsac.carrental.servlet;

import by.bsac.carrental.model.User;
import by.bsac.carrental.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Сервлет авторизации.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/common/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        try {
            User user = userService.authenticate(login, password);
            if (user == null) {
                req.setAttribute("error", "Неверный логин или пароль");
                req.getRequestDispatcher("/WEB-INF/views/common/login.jsp").forward(req, resp);
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);

            if (user.isAdmin()) {
                resp.sendRedirect(req.getContextPath() + "/admin/orders");
            } else {
                resp.sendRedirect(req.getContextPath() + "/client/cars");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
