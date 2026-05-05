-- =====================================================
-- База данных: car_rental (SQLite)
-- Курсовая работа: Система проката автомобилей
-- =====================================================

DROP TABLE IF EXISTS damages;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cars;
DROP TABLE IF EXISTS users;

PRAGMA foreign_keys = ON;

-- Таблица пользователей (клиенты и администраторы)
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    login TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    full_name TEXT NOT NULL,
    email TEXT,
    phone TEXT,
    role TEXT NOT NULL DEFAULT 'CLIENT', -- CLIENT, ADMIN
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Таблица автомобилей
CREATE TABLE cars (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    brand TEXT NOT NULL,
    model TEXT NOT NULL,
    year INTEGER NOT NULL,
    license_plate TEXT UNIQUE NOT NULL,
    color TEXT,
    transmission TEXT,
    fuel_type TEXT,
    price_per_day REAL NOT NULL,
    image_url TEXT,
    description TEXT,
    status TEXT NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE, RENTED, REPAIR
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Таблица заказов
CREATE TABLE orders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    car_id INTEGER NOT NULL REFERENCES cars(id) ON DELETE CASCADE,
    passport_series TEXT NOT NULL,
    passport_number TEXT NOT NULL,
    passport_issued_by TEXT,
    rent_from TEXT NOT NULL,    -- ISO формат YYYY-MM-DD
    rent_to TEXT NOT NULL,
    total_price REAL NOT NULL,
    status TEXT NOT NULL DEFAULT 'PENDING',
    -- PENDING, PAID, REJECTED, COMPLETED, DAMAGED
    rejection_reason TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Таблица платежей
CREATE TABLE payments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    amount REAL NOT NULL,
    payment_type TEXT NOT NULL,  -- RENT, REPAIR
    card_number TEXT,
    payment_date TEXT DEFAULT CURRENT_TIMESTAMP,
    status TEXT NOT NULL DEFAULT 'SUCCESS' -- SUCCESS, FAILED
);

-- Таблица повреждений
CREATE TABLE damages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    description TEXT NOT NULL,
    repair_cost REAL NOT NULL,
    is_paid INTEGER DEFAULT 0,    -- SQLite: 0 = false, 1 = true
    return_date TEXT DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- Тестовые данные
-- =====================================================

-- Администратор: login=admin, password=admin123
INSERT INTO users (login, password, full_name, email, phone, role) VALUES
('admin', 'admin123', 'Администратор Системы', 'admin@carrental.by', '+375291234567', 'ADMIN'),
('ivan', 'ivan123', 'Иванов Иван Иванович', 'ivan@mail.ru', '+375291112233', 'CLIENT'),
('petr', 'petr123', 'Петров Пётр Петрович', 'petr@mail.ru', '+375294445566', 'CLIENT');

-- Автомобили
INSERT INTO cars (brand, model, year, license_plate, color, transmission, fuel_type, price_per_day, image_url, description, status) VALUES
('Toyota', 'Camry', 2022, '1234 AB-7', 'Чёрный', 'AUTO', 'PETROL', 120.00,
 'https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?w=600',
 'Комфортный седан бизнес-класса, отлично подходит для деловых поездок.', 'AVAILABLE'),

('Volkswagen', 'Polo', 2021, '5678 CD-7', 'Белый', 'MANUAL', 'PETROL', 70.00,
 'https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=600',
 'Экономичный и надёжный городской автомобиль.', 'AVAILABLE'),

('BMW', 'X5', 2023, '9012 EF-7', 'Серый', 'AUTO', 'DIESEL', 250.00,
 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=600',
 'Премиальный кроссовер с полным приводом.', 'AVAILABLE'),

('Tesla', 'Model 3', 2023, '3456 GH-7', 'Синий', 'AUTO', 'ELECTRIC', 200.00,
 'https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=600',
 'Электромобиль с автопилотом и большим запасом хода.', 'AVAILABLE'),

('Renault', 'Logan', 2020, '7890 IJ-7', 'Красный', 'MANUAL', 'PETROL', 55.00,
 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=600',
 'Бюджетный автомобиль для семейных поездок.', 'AVAILABLE'),

('Audi', 'A6', 2022, '2345 KL-7', 'Чёрный', 'AUTO', 'DIESEL', 180.00,
 'https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=600',
 'Бизнес-седан премиум-класса с современным салоном.', 'AVAILABLE');
