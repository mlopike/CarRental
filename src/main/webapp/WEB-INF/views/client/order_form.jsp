<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Оформление заказа — Car Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="${pageContext.request.contextPath}/js/main.js" defer></script>
</head>
<body>
<jsp:include page="../common/header.jsp"/>

<main>
    <h1>Оформление заказа</h1>

    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <div class="form-container wide">

        <!-- Информация об автомобиле -->
        <div style="display:flex; gap:1.5rem; align-items:center; margin-bottom:1.5rem;
                    padding-bottom:1.5rem; border-bottom:1px solid #eee; flex-wrap:wrap;">
            <img src="${car.imageUrl}" alt="${car.brand}"
                 style="width:200px; height:130px; object-fit:cover; border-radius:8px;">
            <div>
                <h2 style="margin:0 0 0.4rem;">${car.brand} ${car.model} (${car.year})</h2>
                <p style="color:#666;">Гос. номер: <strong>${car.licensePlate}</strong></p>
                <p style="color:#666;">Цвет: <strong>${car.color}</strong></p>
                <p class="car-price" style="margin-top:0.5rem;">
                    <fmt:formatNumber value="${car.pricePerDay}" maxFractionDigits="2"/> BYN
                    <small>/ сутки</small>
                </p>
            </div>
        </div>

        <form method="post" action="${pageContext.request.contextPath}/client/order">
            <input type="hidden" name="carId" value="${car.id}">
            <input type="hidden" id="pricePerDay" value="${car.pricePerDay}">

            <h2>Паспортные данные</h2>
            <div class="form-row">
                <div class="form-group">
                    <label for="passportSeries">Серия *</label>
                    <input type="text" id="passportSeries" name="passportSeries"
                           class="form-control" required maxlength="10" placeholder="MP">
                </div>
                <div class="form-group">
                    <label for="passportNumber">Номер *</label>
                    <input type="text" id="passportNumber" name="passportNumber"
                           class="form-control" required maxlength="20" placeholder="1234567">
                </div>
            </div>
            <div class="form-group">
                <label for="passportIssuedBy">Кем выдан</label>
                <input type="text" id="passportIssuedBy" name="passportIssuedBy"
                       class="form-control" placeholder="Минским РУВД...">
            </div>

            <h2>Срок аренды</h2>
            <div class="form-row">
                <div class="form-group">
                    <label for="rentFrom">Дата начала *</label>
                    <input type="date" id="rentFrom" name="rentFrom" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="rentTo">Дата окончания *</label>
                    <input type="date" id="rentTo" name="rentTo" class="form-control" required>
                </div>
            </div>

            <div class="alert alert-info">
                <div class="detail-row">
                    <span class="label">Количество дней:</span>
                    <span class="value"><span id="daysCount">0</span></span>
                </div>
                <div class="detail-row">
                    <span class="label">Цена за сутки:</span>
                    <span class="value">
                        <fmt:formatNumber value="${car.pricePerDay}" maxFractionDigits="2"/> BYN
                    </span>
                </div>
                <div class="total-row">
                    <span>Итого к оплате:</span>
                    <span id="totalPrice">0.00 BYN</span>
                </div>
            </div>

            <div style="display:flex; gap:0.7rem; margin-top:1rem;">
                <a href="${pageContext.request.contextPath}/client/cars"
                   class="btn btn-secondary">Назад к каталогу</a>
                <button type="submit" class="btn btn-success" style="flex:1;">
                    Оформить и перейти к оплате
                </button>
            </div>
        </form>
    </div>
</main>

<jsp:include page="../common/footer.jsp"/>
</body>
</html>
