package by.bsac.carrental.filter;

import by.bsac.carrental.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Фильтр проверки авторизации и ролей.
 * - /admin/* доступен только пользователям с ролью ADMIN
 * - /client/* доступен только авторизованным пользователям
 */
@WebFilter(urlPatterns = {"/admin/*", "/client/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        String uri = request.getRequestURI();

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (uri.contains("/admin/") && !user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/client/cars");
            return;
        }

        chain.doFilter(req, resp);
    }
}
