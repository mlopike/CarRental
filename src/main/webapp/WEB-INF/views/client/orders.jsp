<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Мои заказы — Car Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="../common/header.jsp"/>

<main>
    <h1>Мои заказы</h1>

    <c:choose>
        <c:when test="${empty orders}">
            <div class="empty-state">
                <div class="icon">📋</div>
                <p>У вас ещё нет заказов</p>
                <a href="${pageContext.request.contextPath}/client/cars"
                   class="btn btn-primary" style="margin-top:1rem;">Перейти в каталог</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-wrapper">
                <table>
                    <thead>
                    <tr>
                        <th>№</th>
                        <th>Автомобиль</th>
                        <th>Период</th>
                        <th>Сумма</th>
                        <th>Статус</th>
                        <th>Действия</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="o" items="${orders}">
                        <tr>
                            <td>#${o.id}</td>
                            <td>${o.car.brand} ${o.car.model}<br>
                                <small style="color:#999;">${o.car.licensePlate}</small></td>
                            <td>${o.rentFrom} —<br>${o.rentTo}</td>
                            <td><fmt:formatNumber value="${o.totalPrice}" maxFractionDigits="2"/> BYN</td>
                            <td>
                                <c:choose>
                                    <c:when test="${o.status == 'PENDING'}">
                                        <span class="badge badge-pending">Ожидает оплаты</span>
                                    </c:when>
                                    <c:when test="${o.status == 'PAID'}">
                                        <span class="badge badge-paid">Оплачен</span>
                                    </c:when>
                                    <c:when test="${o.status == 'REJECTED'}">
                                        <span class="badge badge-rejected">Отклонён</span>
                                        <br><small style="color:#721c24;">${o.rejectionReason}</small>
                                    </c:when>
                                    <c:when test="${o.status == 'COMPLETED'}">
                                        <span class="badge badge-completed">Завершён</span>
                                    </c:when>
                                    <c:when test="${o.status == 'DAMAGED'}">
                                        <span class="badge badge-damaged">С повреждениями</span>
                                        <c:if test="${not empty damages[o.id]}">
                                            <br><small style="color:#721c24;">${damages[o.id].description}</small><br>
                                            <small>Счёт:
                                                <fmt:formatNumber value="${damages[o.id].repairCost}" maxFractionDigits="2"/> BYN
                                                <c:if test="${damages[o.id].paid}">
                                                    <span class="badge badge-paid">оплачен</span>
                                                </c:if>
                                            </small>
                                        </c:if>
                                    </c:when>
                                </c:choose>
                            </td>
                            <td>
                                <c:if test="${o.status == 'PENDING'}">
                                    <a href="${pageContext.request.contextPath}/client/payment?orderId=${o.id}"
                                       class="btn btn-sm btn-success">💳 Оплатить</a>
                                </c:if>
                                <c:if test="${o.status == 'DAMAGED'
                                              and not empty damages[o.id]
                                              and not damages[o.id].paid}">
                                    <a href="${pageContext.request.contextPath}/client/payment?orderId=${o.id}&type=REPAIR"
                                       class="btn btn-sm btn-warning">💳 Оплатить ремонт</a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="../common/footer.jsp"/>
</body>
</html>
