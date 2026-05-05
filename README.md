# Car Rental System — Курсовая работа

**Тема:** Разработка web-приложения «Система проката автомобилей»  
**Автор:** Верняховский Владислав Дмитриевич  
**ВУЗ:** БГАС, ФЭС, кафедра ПОСТ  
**Руководитель:** Рябычина О.П.

## Стек технологий

| Слой         | Технология                          |
|--------------|-------------------------------------|
| Backend      | Java 11, Servlets API 4.0, JSP, JSTL |
| База данных  | **SQLite 3** (через `sqlite-jdbc`)   |
| Frontend     | HTML5, CSS3, JavaScript (vanilla)    |
| Сборка       | Maven                                |
| Сервер       | Apache Tomcat 9.x                    |

## Структура проекта

```
CarRentalSystem/
├── pom.xml
├── sql/init.sql                            # SQL-скрипт (для ручного просмотра)
├── src/main/
│   ├── java/by/bsac/carrental/
│   │   ├── model/      → User, Car, Order, Payment, Damage
│   │   ├── dao/        → DAO для каждой сущности (JDBC)
│   │   ├── service/    → Бизнес-логика
│   │   ├── servlet/    → Сервлеты-контроллеры
│   │   ├── filter/     → AuthFilter (фильтр авторизации)
│   │   └── util/       → DBConnection (с автоинициализацией БД)
│   ├── resources/
│   │   ├── db.properties                   # Настройки подключения к БД
│   │   └── init.sql                        # Скрипт схемы (для автонакатки)
│   └── webapp/
│       ├── WEB-INF/web.xml
│       ├── WEB-INF/views/                  # JSP-шаблоны
│       │   ├── common/  (login, register, header, footer, error)
│       │   ├── client/  (cars, order_form, payment, payment_success, orders)
│       │   └── admin/   (orders, cars)
│       ├── css/style.css
│       ├── js/main.js
│       └── index.jsp
```

## Запуск проекта

### 1. Никакой установки СУБД не требуется!

SQLite — встроенная файловая БД. JDBC-драйвер (`sqlite-jdbc`) идёт в зависимостях Maven.  
**При первом запуске** приложение само создаст файл БД `car_rental.db` и накатит схему из `init.sql`.

### 2. (Опционально) изменить путь к файлу БД

В `src/main/resources/db.properties`:

```properties
# Файл БД создастся в рабочей директории Tomcat
db.url=jdbc:sqlite:car_rental.db
db.driver=org.sqlite.JDBC
db.init.script=init.sql
```

Для абсолютного пути, например:

```properties
db.url=jdbc:sqlite:C:/data/car_rental.db
```

### 3. Сборка проекта

```bash
mvn clean package
```

Результат — `target/CarRentalSystem.war`.

### 4. Развёртывание на Tomcat

**Вариант A (Eclipse):**
1. Импортируйте проект как Existing Maven Project.
2. ПКМ по проекту → Run As → Run on Server → выберите Tomcat 9.
3. Откройте `http://localhost:8080/CarRentalSystem/`.

**Вариант B (вручную):**
1. Скопируйте `CarRentalSystem.war` в `<TOMCAT_HOME>/webapps/`.
2. Запустите Tomcat (`bin/startup.sh` или `startup.bat`).
3. Откройте `http://localhost:8080/CarRentalSystem/`.

При первом запуске в логах появится сообщение:
```
[DB] Файл БД не найден, инициализация: <путь>/car_rental.db
[DB] Инициализация БД завершена успешно.
```

### 5. Просмотр содержимого БД

Файл `car_rental.db` можно открыть в **DB Browser for SQLite** (бесплатный GUI, аналог pgAdmin).  
Скачать: <https://sqlitebrowser.org/>

## Тестовые учётные записи

| Роль       | Логин   | Пароль    |
|------------|---------|-----------|
| Админ      | admin   | admin123  |
| Клиент 1   | ivan    | ivan123   |
| Клиент 2   | petr    | petr123   |

## Сценарии использования

### Клиент
1. Регистрация / вход в систему
2. Просмотр каталога доступных авто (`/client/cars`)
3. Оформление заказа: выбор авто → форма (паспорт + срок аренды) → расчёт стоимости
4. Оплата (тестовая платёжная система — любой 16-значный номер карты)
5. Просмотр статусов своих заказов (`/client/orders`)
6. Оплата счёта за ремонт, если возврат был с повреждениями

### Администратор
1. Управление заказами (`/admin/orders`) с фильтром по статусу
2. **Отклонение** заявки клиента с указанием причины
3. **Регистрация возврата** автомобиля:
   - без повреждений → заказ COMPLETED, авто AVAILABLE
   - с повреждениями → выставление счёта на ремонт; заказ DAMAGED, авто REPAIR
4. Управление автопарком (`/admin/cars`): добавление / удаление, завершение ремонта

## Жизненный цикл заказа

```
   PENDING (создан)
      │ оплата
      ▼
    PAID (активен) ────── REJECTED (админ отклонил)
      │
      │ возврат
      ├──────────► COMPLETED  (без повреждений)
      └──────────► DAMAGED    (с повреждениями + счёт на ремонт)
```

## Жизненный цикл автомобиля

```
AVAILABLE  ──оплата заказа──►  RENTED
  ▲                              │
  │                              │ возврат
  │                              ▼
  └── (ремонт окончен) ←── REPAIR ◄── возврат с повреждениями
```

## Особенности SQLite

В курсовой пояснительной записке стоит упомянуть особенности перехода на SQLite:

1. **`SERIAL` → `INTEGER PRIMARY KEY AUTOINCREMENT`** — собственный механизм автоинкремента в SQLite.
2. **`BOOLEAN` → `INTEGER` (0/1)** — нативного булева типа нет, поэтому в DAO `is_paid` пишется как `setInt(1/0)` и читается как `getInt(...) == 1`.
3. **`TIMESTAMP` → `TEXT`** в формате `YYYY-MM-DD HH:MM:SS`. Парсинг сделан вручную через `Timestamp.valueOf(...)`.
4. **Даты `rent_from`/`rent_to`** — также `TEXT` в формате `YYYY-MM-DD`, преобразуем в `java.sql.Date` через `Date.valueOf(...)`.
5. **Внешние ключи** включаются командой `PRAGMA foreign_keys = ON` при каждом подключении.
6. **Тип `REAL`** используется вместо `DECIMAL` — JDBC корректно отдаёт его как `BigDecimal`.
