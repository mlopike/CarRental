<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Автопарк — Car Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="${pageContext.request.contextPath}/js/main.js" defer></script>
</head>
<body>
<jsp:include page="../common/header.jsp"/>

<main>
    <div style="display:flex; justify-content:space-between; align-items:center;
                margin-bottom:1.5rem; flex-wrap:wrap; gap:1rem;">
        <h1 style="margin:0;">Автопарк</h1>
        <button class="btn btn-success" onclick="openDialog('addCarDialog')">
            ➕ Добавить автомобиль
        </button>
    </div>

    <c:choose>
        <c:when test="${empty cars}">
            <div class="empty-state">
                <div class="icon">🚗</div>
                <p>Автопарк пуст. Добавьте первый автомобиль.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-wrapper">
                <table>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Авто</th>
                        <th>Гос. номер</th>
                        <th>Год</th>
                        <th>Цвет</th>
                        <th>КПП</th>
                        <th>Топливо</th>
                        <th>Цена/сутки</th>
                        <th>Статус</th>
                        <th>Действия</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="car" items="${cars}">
                        <tr>
                            <td>${car.id}</td>
                            <td><strong>${car.brand} ${car.model}</strong></td>
                            <td>${car.licensePlate}</td>
                            <td>${car.year}</td>
                            <td>${car.color}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${car.transmission == 'AUTO'}">Автомат</c:when>
                                    <c:otherwise>Механика</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${car.fuelType == 'PETROL'}">Бензин</c:when>
                                    <c:when test="${car.fuelType == 'DIESEL'}">Дизель</c:when>
                                    <c:when test="${car.fuelType == 'ELECTRIC'}">Электро</c:when>
                                </c:choose>
                            </td>
                            <td><fmt:formatNumber value="${car.pricePerDay}" maxFractionDigits="2"/> BYN</td>
                            <td>
                                <c:choose>
                                    <c:when test="${car.status == 'AVAILABLE'}">
                                        <span class="badge badge-available">Доступен</span>
                                    </c:when>
                                    <c:when test="${car.status == 'RENTED'}">
                                        <span class="badge badge-rented">В аренде</span>
                                    </c:when>
                                    <c:when test="${car.status == 'REPAIR'}">
                                        <span class="badge badge-repair">В ремонте</span>
                                    </c:when>
                                </c:choose>
                            </td>
                            <td>
                                <c:if test="${car.status == 'REPAIR'}">
                                    <form method="post" style="display:inline;"
                                          action="${pageContext.request.contextPath}/admin/cars">
                                        <input type="hidden" name="action" value="changeStatus">
                                        <input type="hidden" name="id" value="${car.id}">
                                        <input type="hidden" name="status" value="AVAILABLE">
                                        <button type="submit" class="btn btn-sm btn-success"
                                                onclick="return confirm('Ремонт завершён?')">
                                            ✅ Ремонт окончен
                                        </button>
                                    </form>
                                </c:if>
                                <c:if test="${car.status == 'AVAILABLE'}">
                                    <form method="post" style="display:inline;"
                                          action="${pageContext.request.contextPath}/admin/cars">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${car.id}">
                                        <button type="submit" class="btn btn-sm btn-danger"
                                                onclick="return confirm('Удалить автомобиль?');">
                                            🗑
                                        </button>
                                    </form>
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

<!-- Диалог: добавление автомобиля -->
<div id="addCarDialog" class="dialog-overlay">
    <div class="dialog">
        <h3>Новый автомобиль</h3>
        <form method="post" action="${pageContext.request.contextPath}/admin/cars">
            <input type="hidden" name="action" value="add">

            <div class="form-row">
                <div class="form-group">
                    <label>Марка *</label>
                    <input type="text" name="brand" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Модель *</label>
                    <input type="text" name="model" class="form-control" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Год *</label>
                    <input type="number" name="year" class="form-control"
                           min="1990" max="2030" value="2024" required>
                </div>
                <div class="form-group">
                    <label>Гос. номер *</label>
                    <input type="text" name="licensePlate" class="form-control" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Цвет</label>
                    <input type="text" name="color" class="form-control">
                </div>
                <div class="form-group">
                    <label>Цена за сутки (BYN) *</label>
                    <input type="number" name="pricePerDay" class="form-control"
                           min="1" step="0.01" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>КПП</label>
                    <select name="transmission" class="form-control">
                        <option value="AUTO">Автомат</option>
                        <option value="MANUAL">Механика</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Тип топлива</label>
                    <select name="fuelType" class="form-control">
                        <option value="PETROL">Бензин</option>
                        <option value="DIESEL">Дизель</option>
                        <option value="ELECTRIC">Электро</option>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label>URL изображения</label>
                <input type="url" name="imageUrl" class="form-control"
                       placeholder="https://...">
            </div>

            <div class="form-group">
                <label>Описание</label>
                <textarea name="description" class="form-control" rows="2"></textarea>
            </div>

            <div class="dialog-actions">
                <button type="button" class="btn btn-secondary"
                        onclick="closeDialog('addCarDialog')">Отмена</button>
                <button type="submit" class="btn btn-success">Добавить</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>
</body>
</html>
