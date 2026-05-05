<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Регистрация — Car Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<header>
    <div class="header-inner">
        <div class="logo">🚗 Car<span>Rental</span></div>
    </div>
</header>

<main>
    <div class="auth-page">
        <div class="auth-card">
            <h1>Регистрация клиента</h1>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error"><%= request.getAttribute("error") %></div>
            <% } %>

            <form method="post" action="${pageContext.request.contextPath}/register">
                <div class="form-group">
                    <label for="login">Логин *</label>
                    <input type="text" id="login" name="login" class="form-control"
                           required minlength="3" maxlength="50">
                </div>

                <div class="form-group">
                    <label for="password">Пароль * (от 4 символов)</label>
                    <input type="password" id="password" name="password" class="form-control"
                           required minlength="4">
                </div>

                <div class="form-group">
                    <label for="fullName">ФИО *</label>
                    <input type="text" id="fullName" name="fullName" class="form-control" required>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="email">E-mail</label>
                        <input type="email" id="email" name="email" class="form-control">
                    </div>
                    <div class="form-group">
                        <label for="phone">Телефон</label>
                        <input type="tel" id="phone" name="phone" class="form-control"
                               placeholder="+375XXXXXXXXX">
                    </div>
                </div>

                <button type="submit" class="btn btn-primary btn-block">Зарегистрироваться</button>
            </form>

            <div class="switch">
                Уже есть аккаунт?
                <a href="${pageContext.request.contextPath}/login">Войти</a>
            </div>
        </div>
    </div>
</main>

<jsp:include page="footer.jsp"/>

</body>
</html>
