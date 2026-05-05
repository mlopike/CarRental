<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Оплата — Car Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="${pageContext.request.contextPath}/js/main.js" defer></script>
</head>
<body>
<jsp:include page="../common/header.jsp"/>

<main>
    <h1>
        <c:choose>
            <c:when test="${paymentType == 'REPAIR'}">Оплата ремонта</c:when>
            <c:otherwise>Оплата заказа №${order.id}</c:otherwise>
        </c:choose>
    </h1>

    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <div class="form-container">
        <h2>Детали заказа</h2>

        <div class="detail-row">
            <span class="label">Номер заказа:</span>
            <span class="value">№${order.id}</span>
        </div>
        <div class="detail-row">
            <span class="label">Автомобиль:</span>
            <span class="value">${order.car.brand} ${order.car.model}</span>
        </div>
        <div class="detail-row">
            <span class="label">Период аренды:</span>
            <span class="value">${order.rentFrom} — ${order.rentTo}</span>
        </div>

        <c:if test="${paymentType == 'REPAIR' and not empty damage}">
            <div class="detail-row">
                <span class="label">Повреждение:</span>
                <span class="value">${damage.description}</span>
            </div>
        </c:if>

        <div class="total-row">
            <span>Сумма к оплате:</span>
            <span><fmt:formatNumber value="${amount}" maxFractionDigits="2"/> BYN</span>
        </div>

        <h2>Платёжные данные</h2>
        <div class="alert alert-warning" style="font-size:0.85rem;">
            ⚠️ Это <strong>тестовая</strong> платёжная система. Введите любой 16-значный номер карты,
            например <code>4111 1111 1111 1111</code>.
        </div>

        <form method="post" action="${pageContext.request.contextPath}/client/payment">
            <input type="hidden" name="orderId" value="${order.id}">
            <input type="hidden" name="amount" value="${amount}">
            <input type="hidden" name="paymentType" value="${paymentType}">

            <div class="form-group">
                <label for="cardNumber">Номер карты *</label>
                <input type="text" id="cardNumber" name="cardNumber" class="form-control"
                       required maxlength="19" placeholder="0000 0000 0000 0000">
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="expiryDate">Срок (ММ/ГГ) *</label>
                    <input type="text" id="expiryDate" name="expiryDate" class="form-control"
                           required maxlength="5" placeholder="12/28">
                </div>
                <div class="form-group">
                    <label for="cvv">CVV *</label>
                    <input type="text" id="cvv" name="cvv" class="form-control"
                           required maxlength="3" placeholder="123" pattern="\d{3}">
                </div>
            </div>

            <div class="form-group">
                <label for="cardHolder">Имя владельца *</label>
                <input type="text" id="cardHolder" name="cardHolder" class="form-control"
                       required placeholder="IVAN IVANOV">
            </div>

            <div style="display:flex; gap:0.7rem; margin-top:1rem;">
                <a href="${pageContext.request.contextPath}/client/orders" class="btn btn-secondary">
                    Отмена
                </a>
                <button type="submit" class="btn btn-success" style="flex:1;">
                    💳 Оплатить <fmt:formatNumber value="${amount}" maxFractionDigits="2"/> BYN
                </button>
            </div>
        </form>
    </div>
</main>

<jsp:include page="../common/footer.jsp"/>
</body>
</html>
