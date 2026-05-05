<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header>
    <div class="header-inner">
        <div class="logo">🚗 Car<span>Rental</span></div>
        <nav>
            <ul>
                <c:choose>
                    <c:when test="${not empty sessionScope.user and sessionScope.user.role == 'ADMIN'}">
                        <li><a href="${pageContext.request.contextPath}/admin/orders">Заказы</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/cars">Автопарк</a></li>
                    </c:when>
                    <c:when test="${not empty sessionScope.user}">
                        <li><a href="${pageContext.request.contextPath}/client/cars">Каталог</a></li>
                        <li><a href="${pageContext.request.contextPath}/client/orders">Мои заказы</a></li>
                    </c:when>
                </c:choose>
            </ul>
        </nav>
        <div class="user-info">
            <c:if test="${not empty sessionScope.user}">
                <span class="username">👤 ${sessionScope.user.fullName}</span>
                <a href="${pageContext.request.contextPath}/logout"
                   class="btn btn-sm btn-secondary">Выход</a>
            </c:if>
        </div>
    </div>
</header>
