<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Ошибка — Car Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="header.jsp"/>
<main>
    <div class="auth-page">
        <div class="auth-card">
            <h1 style="text-align:center;">Произошла ошибка</h1>
            <p style="text-align:center; color:#666;">
                Попробуйте позже или вернитесь на главную.
            </p>
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary btn-block"
               style="margin-top:1rem;">На главную</a>
        </div>
    </div>
</main>
<jsp:include page="footer.jsp"/>
</body>
</html>
