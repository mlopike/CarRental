<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Оплата успешна — Car Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="../common/header.jsp"/>

<main>
    <div class="auth-page">
        <div class="auth-card" style="text-align:center;">
            <div style="font-size:4rem; margin-bottom:1rem;">✅</div>
            <h1>Оплата прошла успешно!</h1>
            <p style="color:#666; margin: 1rem 0;">
                <c:choose>
                    <c:when test="${paymentType == 'REPAIR'}">
                        Спасибо! Счёт за ремонт оплачен.
                    </c:when>
                    <c:otherwise>
                        Спасибо за заказ! Теперь администратор свяжется с вами для передачи автомобиля.
                    </c:otherwise>
                </c:choose>
            </p>
            <a href="${pageContext.request.contextPath}/client/orders"
               class="btn btn-primary btn-block">Перейти к моим заказам</a>
        </div>
    </div>
</main>

<jsp:include page="../common/footer.jsp"/>
</body>
</html>
