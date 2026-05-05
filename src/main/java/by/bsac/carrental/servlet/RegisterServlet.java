package by.bsac.carrental.servlet;

import by.bsac.carrental.model.User;
import by.bsac.carrental.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Сервлет регистрации нового клиента.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/common/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String login    = req.getParameter("login");
        String password = req.getParameter("password");
        String fullName = req.getParameter("fullName");
        String email    = req.getParameter("email");
        String phone    = req.getParameter("phone");

        if (login == null || login.trim().isEmpty()
            || password == null || password.length() < 4
            || fullName == null || fullName.trim().isEmpty()) {
            req.setAttribute("error", "Заполните обязательные поля. Пароль не короче 4 символов.");
            req.getRequestDispatcher("/WEB-INF/views/common/register.jsp").forward(req, resp);
            return;
        }

        try {
            User user = new User(login.trim(), password, fullName.trim(),
                                 email, phone, "CLIENT");
            boolean ok = userService.register(user);
            if (!ok) {
                req.setAttribute("error", "Логин уже занят");
                req.getRequestDispatcher("/WEB-INF/views/common/register.jsp").forward(req, resp);
                return;
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/client/cars");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
