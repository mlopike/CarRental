<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Каталог автомобилей — Car Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="../common/header.jsp"/>

<main>
    <h1>Доступные автомобили</h1>

    <c:choose>
        <c:when test="${empty cars}">
            <div class="empty-state">
                <div class="icon">🚙</div>
                <p>В данный момент нет доступных автомобилей</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="cars-grid">
                <c:forEach var="car" items="${cars}">
                    <div class="car-card">
                        <img src="${car.imageUrl}" alt="${car.brand} ${car.model}"
                             onerror="this.src='https://via.placeholder.com/400x180?text=No+Image'">
                        <div class="car-body">
                            <div class="car-title">${car.brand} ${car.model}</div>
                            <div class="car-info">
                                <span>📅 ${car.year}</span>
                                <span>🎨 ${car.color}</span>
                                <span>⚙️
                                    <c:choose>
                                        <c:when test="${car.transmission == 'AUTO'}">Автомат</c:when>
                                        <c:otherwise>Механика</c:otherwise>
                                    </c:choose>
                                </span>
                                <span>⛽
                                    <c:choose>
                                        <c:when test="${car.fuelType == 'PETROL'}">Бензин</c:when>
                                        <c:when test="${car.fuelType == 'DIESEL'}">Дизель</c:when>
                                        <c:when test="${car.fuelType == 'ELECTRIC'}">Электро</c:when>
                                        <c:otherwise>${car.fuelType}</c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                            <p style="color:#666; font-size:0.9rem; margin-bottom:0.7rem;">
                                ${car.description}
                            </p>
                            <div class="car-price">
                                <fmt:formatNumber value="${car.pricePerDay}" maxFractionDigits="2"/> BYN
                                <small>/ сутки</small>
                            </div>
                            <a href="${pageContext.request.contextPath}/client/order?carId=${car.id}"
                               class="btn btn-primary">Арендовать</a>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="../common/footer.jsp"/>
</body>
</html>
