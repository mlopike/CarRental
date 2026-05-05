<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Вход — Car Rental</title>
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
            <h1>Вход в систему</h1>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error"><%= request.getAttribute("error") %></div>
            <% } %>

            <form method="post" action="${pageContext.request.contextPath}/login">
                <div class="form-group">
                    <label for="login">Логин</label>
                    <input type="text" id="login" name="login" class="form-control" required autofocus>
                </div>

                <div class="form-group">
                    <label for="password">Пароль</label>
                    <input type="password" id="password" name="password" class="form-control" required>
                </div>

                <button type="submit" class="btn btn-primary btn-block">Войти</button>
            </form>

            <div class="switch">
                Нет аккаунта?
                <a href="${pageContext.request.contextPath}/register">Зарегистрироваться</a>
            </div>

            <div class="alert alert-info" style="margin-top:1.2rem; font-size:0.85rem;">
                <strong>Тестовые учётки:</strong><br>
                Админ — <code>admin / admin123</code><br>
                Клиент — <code>ivan / ivan123</code>
            </div>
        </div>
    </div>
</main>

<jsp:include page="footer.jsp"/>

</body>
</html>
