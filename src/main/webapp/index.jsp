<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session != null && session.getAttribute("user") != null) {
        Object u = session.getAttribute("user");
        if (u instanceof by.bsac.carrental.model.User
            && ((by.bsac.carrental.model.User) u).isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        } else {
            response.sendRedirect(request.getContextPath() + "/client/cars");
        }
    } else {
        response.sendRedirect(request.getContextPath() + "/login");
    }
%>
