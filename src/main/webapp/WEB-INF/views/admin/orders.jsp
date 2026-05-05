<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Управление заказами — Car Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="${pageContext.request.contextPath}/js/main.js" defer></script>
</head>
<body>
<jsp:include page="../common/header.jsp"/>

<main>
    <h1>Управление заказами</h1>

    <div class="filters">
        <a href="?status=ALL"      class="${currentStatus == 'ALL' ? 'active' : ''}">Все</a>
        <a href="?status=PENDING"  class="${currentStatus == 'PENDING' ? 'active' : ''}">Ожидают оплаты</a>
        <a href="?status=PAID"     class="${currentStatus == 'PAID' ? 'active' : ''}">Активные</a>
        <a href="?status=DAMAGED"  class="${currentStatus == 'DAMAGED' ? 'active' : ''}">С повреждениями</a>
        <a href="?status=COMPLETED" class="${currentStatus == 'COMPLETED' ? 'active' : ''}">Завершённые</a>
        <a href="?status=REJECTED" class="${currentStatus == 'REJECTED' ? 'active' : ''}">Отклонённые</a>
    </div>

    <c:choose>
        <c:when test="${empty orders}">
            <div class="empty-state">
                <div class="icon">📋</div>
                <p>Нет заказов в выбранной категории</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-wrapper">
                <table>
                    <thead>
                    <tr>
                        <th>№</th>
                        <th>Клиент</th>
                        <th>Автомобиль</th>
                        <th>Паспорт</th>
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
                            <td>${o.userFullName}</td>
                            <td>${o.car.brand} ${o.car.model}<br>
                                <small style="color:#999;">${o.car.licensePlate}</small></td>
                            <td><small>${o.passportSeries} ${o.passportNumber}</small></td>
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
                                        <br><small>${o.rejectionReason}</small>
                                    </c:when>
                                    <c:when test="${o.status == 'COMPLETED'}">
                                        <span class="badge badge-completed">Завершён</span>
                                    </c:when>
                                    <c:when test="${o.status == 'DAMAGED'}">
                                        <span class="badge badge-damaged">С повреждениями</span>
                                        <c:if test="${not empty damages[o.id]}">
                                            <br><small>${damages[o.id].description}</small><br>
                                            <small>Счёт:
                                                <fmt:formatNumber value="${damages[o.id].repairCost}" maxFractionDigits="2"/> BYN
                                                <c:if test="${damages[o.id].paid}">
                                                    <span class="badge badge-paid">опл.</span>
                                                </c:if>
                                            </small>
                                        </c:if>
                                    </c:when>
                                </c:choose>
                            </td>
                            <td>
                                <c:if test="${o.status == 'PENDING'}">
                                    <button class="btn btn-sm btn-danger"
                                            onclick="openRejectDialog(${o.id})">Отклонить</button>
                                </c:if>
                                <c:if test="${o.status == 'PAID'}">
                                    <button class="btn btn-sm btn-success"
                                            onclick="openReturnOk(${o.id})">Возврат OK</button>
                                    <button class="btn btn-sm btn-warning"
                                            onclick="openDamageDialog(${o.id})">⚠ Повреждения</button>
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

<!-- Диалог: отклонение заявки -->
<div id="rejectDialog" class="dialog-overlay">
    <div class="dialog">
        <h3>Отклонить заявку</h3>
        <form method="post" action="${pageContext.request.contextPath}/admin/reject">
            <input type="hidden" id="rejectOrderId" name="orderId">
            <div class="form-group">
                <label for="reason">Причина отказа *</label>
                <textarea id="reason" name="reason" class="form-control" rows="4" required
                          placeholder="Например: некорректные паспортные данные"></textarea>
            </div>
            <div class="dialog-actions">
                <button type="button" class="btn btn-secondary" onclick="closeDialog('rejectDialog')">
                    Отмена
                </button>
                <button type="submit" class="btn btn-danger">Отклонить</button>
            </div>
        </form>
    </div>
</div>

<!-- Скрытая форма: возврат без повреждений -->
<form id="returnOkForm" method="post" action="${pageContext.request.contextPath}/admin/return"
      style="display:none;">
    <input type="hidden" id="returnOkOrderId" name="orderId">
    <input type="hidden" name="returnType" value="OK">
</form>

<!-- Диалог: возврат с повреждениями -->
<div id="damageDialog" class="dialog-overlay">
    <div class="dialog">
        <h3>Регистрация повреждений</h3>
        <form method="post" action="${pageContext.request.contextPath}/admin/return">
            <input type="hidden" id="damageOrderId" name="orderId">
            <input type="hidden" name="returnType" value="DAMAGED">
            <div class="form-group">
                <label for="description">Описание повреждений *</label>
                <textarea id="description" name="description" class="form-control" rows="3" required
                          placeholder="Например: царапина на правой передней двери"></textarea>
            </div>
            <div class="form-group">
                <label for="repairCost">Стоимость ремонта (BYN) *</label>
                <input type="number" id="repairCost" name="repairCost" class="form-control"
                       min="0.01" step="0.01" required>
            </div>
            <div class="dialog-actions">
                <button type="button" class="btn btn-secondary" onclick="closeDialog('damageDialog')">
                    Отмена
                </button>
                <button type="submit" class="btn btn-warning">Выставить счёт</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>
</body>
</html>
