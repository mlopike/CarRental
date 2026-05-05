# Технический анализ проекта "Система проката автомобилей"

## 1. ENTITY-КЛАССЫ (MODEL)

Проект использует **POJO-классы** (Plain Old Java Objects) без JPA/Hibernate аннотаций. Работа с БД осуществляется через JDBC.

### Список entity-классов:

| Класс | Описание | Поля |
|-------|----------|------|
| **Car** | Автомобиль | `id int`, `brand String`, `model String`, `year int`, `licensePlate String`, `color String`, `transmission String`, `fuelType String`, `pricePerDay BigDecimal`, `imageUrl String`, `description String`, `status String` |
| **User** | Пользователь | `id int`, `login String`, `password String`, `fullName String`, `email String`, `phone String`, `role String`, `createdAt Timestamp` |
| **Order** | Заказ аренды | `id int`, `userId int`, `carId int`, `passportSeries String`, `passportNumber String`, `passportIssuedBy String`, `rentFrom Date`, `rentTo Date`, `totalPrice BigDecimal`, `status String`, `rejectionReason String`, `createdAt Timestamp`, `userFullName String`, `car Car` |
| **Payment** | Платёж | `id int`, `orderId int`, `amount BigDecimal`, `paymentType String`, `cardNumber String`, `paymentDate Timestamp`, `status String` |
| **Damage** | Повреждение | `id int`, `orderId int`, `description String`, `repairCost BigDecimal`, `isPaid boolean`, `returnDate Timestamp` |

### Связи между сущностями:
- **User → Order**: One-to-Many (один пользователь может иметь много заказов)
- **Car → Order**: One-to-Many (один автомобиль может быть в многих заказах)
- **Order → Payment**: One-to-Many (один заказ может иметь несколько платежей)
- **Order → Damage**: One-to-One (один заказ может иметь одну запись о повреждении)

---

## 2. СТРУКТУРА БАЗЫ ДАННЫХ (SQLite)

### Таблицы:

#### **users**
| Поле | Тип | Описание |
|------|-----|----------|
| id | INTEGER PRIMARY KEY AUTOINCREMENT | Первичный ключ |
| login | TEXT UNIQUE NOT NULL | Логин пользователя |
| password | TEXT NOT NULL | Пароль (в открытом виде) |
| full_name | TEXT NOT NULL | ФИО |
| email | TEXT | Email |
| phone | TEXT | Телефон |
| role | TEXT NOT NULL DEFAULT 'CLIENT' | Роль: CLIENT, ADMIN |
| created_at | TEXT DEFAULT CURRENT_TIMESTAMP | Дата создания |

#### **cars**
| Поле | Тип | Описание |
|------|-----|----------|
| id | INTEGER PRIMARY KEY AUTOINCREMENT | Первичный ключ |
| brand | TEXT NOT NULL | Марка авто |
| model | TEXT NOT NULL | Модель |
| year | INTEGER NOT NULL | Год выпуска |
| license_plate | TEXT UNIQUE NOT NULL | Номерной знак |
| color | TEXT | Цвет |
| transmission | TEXT | Трансмиссия: AUTO, MANUAL |
| fuel_type | TEXT | Тип топлива: PETROL, DIESEL, ELECTRIC |
| price_per_day | REAL NOT NULL | Цена за день |
| image_url | TEXT | URL изображения |
| description | TEXT | Описание |
| status | TEXT NOT NULL DEFAULT 'AVAILABLE' | Статус: AVAILABLE, RENTED, REPAIR |
| created_at | TEXT DEFAULT CURRENT_TIMESTAMP | Дата создания |

#### **orders**
| Поле | Тип | Описание |
|------|-----|----------|
| id | INTEGER PRIMARY KEY AUTOINCREMENT | Первичный ключ |
| user_id | INTEGER NOT NULL | Внешний ключ → users(id) ON DELETE CASCADE |
| car_id | INTEGER NOT NULL | Внешний ключ → cars(id) ON DELETE CASCADE |
| passport_series | TEXT NOT NULL | Серия паспорта |
| passport_number | TEXT NOT NULL | Номер паспорта |
| passport_issued_by | TEXT | Кем выдан |
| rent_from | TEXT NOT NULL | Дата начала аренды (YYYY-MM-DD) |
| rent_to | TEXT NOT NULL | Дата окончания аренды |
| total_price | REAL NOT NULL | Общая стоимость |
| status | TEXT NOT NULL DEFAULT 'PENDING' | Статус: PENDING, PAID, REJECTED, COMPLETED, DAMAGED |
| rejection_reason | TEXT | Причина отклонения |
| created_at | TEXT DEFAULT CURRENT_TIMESTAMP | Дата создания |

#### **payments**
| Поле | Тип | Описание |
|------|-----|----------|
| id | INTEGER PRIMARY KEY AUTOINCREMENT | Первичный ключ |
| order_id | INTEGER NOT NULL | Внешний ключ → orders(id) ON DELETE CASCADE |
| amount | REAL NOT NULL | Сумма платежа |
| payment_type | TEXT NOT NULL | Тип: RENT, REPAIR |
| card_number | TEXT | Номер карты (маскированный) |
| payment_date | TEXT DEFAULT CURRENT_TIMESTAMP | Дата платежа |
| status | TEXT NOT NULL DEFAULT 'SUCCESS' | Статус: SUCCESS, FAILED |

#### **damages**
| Поле | Тип | Описание |
|------|-----|----------|
| id | INTEGER PRIMARY KEY AUTOINCREMENT | Первичный ключ |
| order_id | INTEGER NOT NULL | Внешний ключ → orders(id) ON DELETE CASCADE |
| description | TEXT NOT NULL | Описание повреждения |
| repair_cost | REAL NOT NULL | Стоимость ремонта |
| is_paid | INTEGER DEFAULT 0 | Оплачено: 0=false, 1=true |
| return_date | TEXT DEFAULT CURRENT_TIMESTAMP | Дата возврата |

---

## 3. АРХИТЕКТУРА ПРИЛОЖЕНИЯ

### Структура пакетов:
```
by.bsac.carrental/
├── model/          # Entity-классы (Car, User, Order, Payment, Damage)
├── dao/            # Data Access Object (CarDAO, UserDAO, OrderDAO, PaymentDAO, DamageDAO)
├── service/        # Бизнес-логика (CarService, UserService, OrderService, PaymentService, DamageService)
├── servlet/        # Контроллеры (LoginServlet, OrderServlet, PaymentServlet, и др.)
├── filter/         # Фильтры (AuthFilter)
├── util/           # Утилиты (DBConnection)
```

### Контроллеры (Servlets) и их эндпоинты:

| Servlet | URL Pattern | Методы | Описание |
|---------|-------------|--------|----------|
| **LoginServlet** | `/login` | GET: форма входа<br>POST: аутентификация | Авторизация пользователей |
| **RegisterServlet** | `/register` | GET: форма регистрации<br>POST: создание пользователя | Регистрация новых клиентов |
| **LogoutServlet** | `/logout` | GET | Завершение сессии |
| **CarListServlet** | `/client/cars` | GET | Список доступных авто для клиента |
| **OrderServlet** | `/client/order` | GET: форма заказа<br>POST: создание заказа | Оформление аренды |
| **ClientOrdersServlet** | `/client/orders` | GET | Мои заказы клиента |
| **PaymentServlet** | `/client/payment` | GET: форма оплаты<br>POST: обработка платежа | Оплата аренды/ремонта |
| **AdminCarsServlet** | `/admin/cars` | GET: список авто<br>POST: добавить/удалить/изменить статус | Управление автопарком |
| **AdminOrdersServlet** | `/admin/orders` | GET | Все заказы с фильтром по статусу |
| **AdminRejectServlet** | `/admin/reject` | POST | Отклонение заявки |
| **AdminReturnServlet** | `/admin/return` | POST | Регистрация возврата авто |

### Сервисы и их основные методы:

| Сервис | Методы |
|--------|--------|
| **UserService** | `authenticate(login, password)`, `register(User)` |
| **CarService** | `getAvailableCars()`, `getAllCars()`, `getCar(id)`, `addCar(Car)`, `updateStatus(id, status)`, `deleteCar(id)` |
| **OrderService** | `createOrder(...)`, `markPaid(orderId)`, `rejectOrder(orderId, reason)`, `completeOrder(orderId)`, `returnDamaged(orderId)`, `getOrder(id)`, `getOrdersByUser(userId)`, `getAllOrders()`, `getOrdersByStatus(status)` |
| **PaymentService** | `processPayment(orderId, amount, cardNumber, paymentType)` |
| **DamageService** | `registerDamage(orderId, description, repairCost)`, `getByOrderId(orderId)`, `markPaid(damageId)` |

### DAO-интерфейсы (классы):
- **CarDAO**: `findAll()`, `findAvailable()`, `findById(id)`, `updateStatus(id, status)`, `save(Car)`, `delete(id)`
- **UserDAO**: `findByLoginAndPassword(login, password)`, `findByLogin(login)`, `save(User)`
- **OrderDAO**: `save(Order)`, `findByUserId(userId)`, `findAll()`, `findByStatus(status)`, `findById(id)`, `updateStatus(id, status)`, `reject(id, reason)`
- **PaymentDAO**: `save(Payment)`, `findByOrderId(orderId)`
- **DamageDAO**: `save(Damage)`, `findByOrderId(orderId)`, `findAll()`, `markPaid(id)`

---

## 4. ШАБЛОНЫ ПРОЕКТИРОВАНИЯ GoF

### Использованные паттерны:

#### 1. **DAO (Data Access Object)** - Вариация паттерна Repository
- **Где реализован**: Все классы в пакете `dao/` (CarDAO, UserDAO, OrderDAO, PaymentDAO, DamageDAO)
- **Описание**: Инкапсулирует логику доступа к базе данных, предоставляя сервисам простой API для CRUD-операций
- **Преимущество**: Разделение бизнес-логики и логики доступа к данным

#### 2. **MVC (Model-View-Controller)** - Архитектурный паттерн
- **Model**: Классы в `model/` (Car, User, Order, Payment, Damage)
- **View**: JSP-страницы в `/WEB-INF/views/`
- **Controller**: Servlets в `servlet/`
- **Описание**: Разделение данных, представления и управляющей логики

#### 3. **Front Controller** (частично)
- **Где реализован**: `AuthFilter.java`
- **Описание**: Фильтр перехватывает запросы к `/admin/*` и `/client/*`, осуществляя единую проверку авторизации

#### 4. **Singleton** (ленивая инициализация)
- **Где реализован**: `DBConnection.java`
- **Описание**: Статический блок инициализации загружает конфигуровку один раз при первом обращении

#### 5. **Strategy** (неявно)
- **Где реализован**: Обработка разных типов платежей (RENT/REPAIR) в `PaymentServlet` и `PaymentService`
- **Описание**: Разная логика обработки в зависимости от типа платежа

---

## 5. ТЕХНОЛОГИЧЕСКИЙ СТЕК

| Компонент | Технология | Версия |
|-----------|------------|--------|
| **Язык** | Java | 11 |
| **Сборка** | Maven | 3.x (maven-war-plugin 3.4.0) |
| **Web-контейнер** | Servlet Container | Servlet API 4.0.1 |
| **JSP** | JSP API | 2.3.3 |
| **Шаблонизация** | JSTL | 1.2 |
| **СУБД** | SQLite | 3.x (sqlite-jdbc 3.45.3.0) |
| **ORM** | Нет (чистый JDBC) | - |
| **Пул соединений** | Нет (DriverManager) | - |
| **UI** | JSP + HTML + CSS + JavaScript | - |
| **Spring Boot** | Не используется | - |
| **Hibernate** | Не используется | - |

**Важное замечание**: Проект НЕ использует Spring Framework или Hibernate. Это традиционное Java EE веб-приложение на сервлетах с прямым JDBC-доступом к базе данных SQLite.

---

## 6. БИЗНЕС-ЛОГИКА

### Основные алгоритмы:

#### 1. **Создание заказа (OrderService.createOrder)**
```java
// Проверка доступности автомобиля
Car car = carDAO.findById(carId);
if (car == null || !"AVAILABLE".equals(car.getStatus())) return null;

// Расчёт стоимости: цена_за_день × количество_дней
long days = ChronoUnit.DAYS.between(rentFrom, rentTo);
BigDecimal total = car.getPricePerDay().multiply(BigDecimal.valueOf(days));

// Создание заказа со статусом PENDING
order.setStatus("PENDING");
orderDAO.save(order);
```

#### 2. **Оплата заказа (OrderService.markPaid)**
```java
// Изменение статуса заказа на PAID
orderDAO.updateStatus(orderId, "PAID");
// Изменение статуса автомобиля на RENTED
carDAO.updateStatus(carId, "RENTED");
```

#### 3. **Возврат автомобиля**
- **Без повреждений**: `completeOrder()` → статус COMPLETED, авто → AVAILABLE
- **С повреждениями**: `returnDamaged()` → статус DAMAGED, авто → REPAIR, создание записи Damage

#### 4. **Обработка платежа (PaymentService.processPayment)**
```java
// Валидация номера карты (16 цифр)
boolean valid = cleanCard.matches("\\d{16}");
payment.setStatus(valid ? "SUCCESS" : "FAILED");
paymentDAO.save(payment);
return valid;
```

### Работа с сессиями:
- **Атрибут сессии**: `session.setAttribute("user", User)`
- **Проверка авторизации**: `AuthFilter` проверяет наличие атрибута "user"
- **Таймаут сессии**: 30 минут (настроено в web.xml)

### Валидация:
- **Регистрация**: проверка уникальности логина, длина пароля ≥ 4 символов
- **Заказ**: проверка дат (rentTo > rentFrom > сегодня), заполненность паспортных данных
- **Оплата**: валидация номера карты (16 цифр)

---

## 7. ПРИМЕРЫ КОДА

### 7.1 Entity-класс Order.java
```java
public class Order {
    private int id;
    private int userId;
    private int carId;
    private String passportSeries;
    private String passportNumber;
    private String passportIssuedBy;
    private Date rentFrom;
    private Date rentTo;
    private BigDecimal totalPrice;
    private String status; // PENDING, PAID, REJECTED, COMPLETED, DAMAGED
    private String rejectionReason;
    private Timestamp createdAt;
    
    // Связанные объекты (подгружаются JOIN)
    private String userFullName;
    private Car car;
    
    // Геттеры и сеттеры...
    
    public String getStatusLabel() {
        switch (status) {
            case "PENDING":   return "Ожидает оплаты";
            case "PAID":      return "Оплачен (активен)";
            case "REJECTED":  return "Отклонён";
            case "COMPLETED": return "Завершён";
            case "DAMAGED":   return "Возвращён с повреждениями";
            default:          return status;
        }
    }
}
```

### 7.2 Контроллер OrderServlet.java
```java
@WebServlet("/client/order")
public class OrderServlet extends HttpServlet {
    private final OrderService orderService = new OrderService();
    private final CarService carService = new CarService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int carId = Integer.parseInt(req.getParameter("carId"));
            Car car = carService.getCar(carId);
            if (car == null || !"AVAILABLE".equals(car.getStatus())) {
                resp.sendRedirect(req.getContextPath() + "/client/cars");
                return;
            }
            req.setAttribute("car", car);
            req.getRequestDispatcher("/WEB-INF/views/client/order_form.jsp")
               .forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        try {
            int carId = Integer.parseInt(req.getParameter("carId"));
            LocalDate from = LocalDate.parse(req.getParameter("rentFrom"));
            LocalDate to = LocalDate.parse(req.getParameter("rentTo"));
            
            // Валидация
            if (from.isAfter(to) || from.equals(to) || from.isBefore(LocalDate.now())) {
                req.setAttribute("error", "Проверьте корректность дат");
                // ... forward back to form
            }
            
            Order order = orderService.createOrder(user.getId(), carId,
                req.getParameter("passportSeries"),
                req.getParameter("passportNumber"),
                req.getParameter("passportIssuedBy"),
                from, to);
            
            resp.sendRedirect(req.getContextPath() + "/client/payment?orderId=" + order.getId());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
```

### 7.3 Сервис OrderService.java
```java
public class OrderService {
    private final OrderDAO orderDAO = new OrderDAO();
    private final CarDAO carDAO = new CarDAO();

    public Order createOrder(int userId, int carId,
                             String passportSeries, String passportNumber,
                             String passportIssuedBy,
                             LocalDate rentFrom, LocalDate rentTo) throws SQLException {
        Car car = carDAO.findById(carId);
        if (car == null || !"AVAILABLE".equals(car.getStatus())) {
            return null;
        }
        if (rentTo.isBefore(rentFrom) || rentTo.equals(rentFrom)) {
            return null;
        }

        long days = ChronoUnit.DAYS.between(rentFrom, rentTo);
        BigDecimal total = car.getPricePerDay().multiply(BigDecimal.valueOf(days));

        Order order = new Order();
        order.setUserId(userId);
        order.setCarId(carId);
        order.setPassportSeries(passportSeries);
        order.setPassportNumber(passportNumber);
        order.setPassportIssuedBy(passportIssuedBy);
        order.setRentFrom(Date.valueOf(rentFrom));
        order.setRentTo(Date.valueOf(rentTo));
        order.setTotalPrice(total);
        order.setStatus("PENDING");

        orderDAO.save(order);
        return order;
    }

    public void markPaid(int orderId) throws SQLException {
        Order order = orderDAO.findById(orderId);
        if (order == null) return;
        orderDAO.updateStatus(orderId, "PAID");
        carDAO.updateStatus(order.getCarId(), "RENTED");
    }
}
```

### 7.4 Конфигурация подключения к БД (db.properties)
```properties
# Путь к файлу БД SQLite
db.url=jdbc:sqlite:car_rental.db
db.driver=org.sqlite.JDBC

# Путь к init-скрипту (относительно classpath)
db.init.script=init.sql
```

### 7.5 pom.xml (основные зависимости)
```xml
<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
</properties>

<dependencies>
    <!-- Servlet API -->
    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>javax.servlet-api</artifactId>
        <version>4.0.1</version>
        <scope>provided</scope>
    </dependency>

    <!-- JSP API -->
    <dependency>
        <groupId>javax.servlet.jsp</groupId>
        <artifactId>javax.servlet.jsp-api</artifactId>
        <version>2.3.3</version>
        <scope>provided</scope>
    </dependency>

    <!-- JSTL -->
    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>jstl</artifactId>
        <version>1.2</version>
    </dependency>

    <!-- SQLite JDBC Driver -->
    <dependency>
        <groupId>org.xerial</groupId>
        <artifactId>sqlite-jdbc</artifactId>
        <version>3.45.3.0</version>
    </dependency>
</dependencies>
```

---

## 8. ДИАГРАММЫ

### ER-диаграмма базы данных:

```
┌─────────────────┐       ┌─────────────────┐
│     users       │       │      cars       │
├─────────────────┤       ├─────────────────┤
│ id (PK)         │       │ id (PK)         │
│ login           │       │ brand           │
│ password        │       │ model           │
│ full_name       │       │ year            │
│ email           │       │ license_plate   │
│ phone           │       │ color           │
│ role            │       │ transmission    │
│ created_at      │       │ fuel_type       │
└────────┬────────┘       │ price_per_day   │
         │                │ image_url       │
         │ 1              │ description     │
         │                │ status          │
         │ N              │ created_at      │
         │                └────────┬────────┘
         │                         │
         │ 1                       │ 1
         ▼                         ▼
┌─────────────────────────────────────────────────┐
│                    orders                        │
├─────────────────────────────────────────────────┤
│ id (PK)                                         │
│ user_id (FK → users.id)                         │
│ car_id (FK → cars.id)                           │
│ passport_series                                 │
│ passport_number                                 │
│ passport_issued_by                              │
│ rent_from                                       │
│ rent_to                                         │
│ total_price                                     │
│ status (PENDING/PAID/REJECTED/COMPLETED/DAMAGED)│
│ rejection_reason                                │
│ created_at                                      │
└────────────────────┬────────────────────────────┘
                     │
           ┌─────────┴──────────┐
           │ 1                  │ 1
           ▼                    ▼
┌─────────────────┐    ┌─────────────────┐
│    payments     │    │    damages      │
├─────────────────┤    ├─────────────────┤
│ id (PK)         │    │ id (PK)         │
│ order_id (FK)   │    │ order_id (FK)   │
│ amount          │    │ description     │
│ payment_type    │    │ repair_cost     │
│ card_number     │    │ is_paid         │
│ payment_date    │    │ return_date     │
│ status          │    └─────────────────┘
└─────────────────┘
```

### Диаграмма классов (упрощённая):

```
┌─────────────────┐
│     Car         │
├─────────────────┤
│ -id: int        │
│ -brand: String  │
│ -model: String  │
│ -pricePerDay: BigDecimal │
│ -status: String │
└─────────────────┘
          ▲
          │ 1
          │
          │ N
┌─────────────────┐       ┌─────────────────┐
│     Order       │       │     User        │
├─────────────────┤       ├─────────────────┤
│ -id: int        │       │ -id: int        │
│ -userId: int    │       │ -login: String  │
│ -carId: int     │       │ -password: Str  │
│ -status: String │       │ -role: String   │
│ -totalPrice: BD │       └─────────────────┘
└─────────────────┘
          │
          │ 1
          │
     ┌────┴────┐
     │         │
     ▼         ▼
┌─────────┐ ┌──────────┐
│ Payment │ │ Damage   │
├─────────┤ ├──────────┤
│ -amount │ │ -desc    │
│ -type   │ │ -cost    │
│ -status │ │ -isPaid  │
└─────────┘ └──────────┘
```

---

## РЕЗЮМЕ

Это **традиционное Java EE веб-приложение** (НЕ Spring Boot) для системы проката автомобилей со следующим функционалом:

### Для клиентов:
- Просмотр доступных автомобилей
- Оформление заказа на аренду
- Оплата аренды
- Просмотр истории заказов

### Для администраторов:
- Управление автопарком (добавление, удаление, изменение статуса)
- Просмотр всех заказов
- Одобрение/отклонение заявок
- Регистрация возврата автомобилей

### Ключевые особенности:
- Чистый JDBC без ORM
- SQLite как файловая БД
- JSP + JSTL для представлений
- MVC-архитектура с сервлетами
- Ролевая модель (CLIENT/ADMIN)
- Полный цикл аренды: заказ → оплата → использование → возврат
