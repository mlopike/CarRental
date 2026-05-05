package by.bsac.carrental.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Утилита для получения соединения с базой данных SQLite.
 *
 * При первом подключении проверяет, существует ли файл БД.
 * Если нет — создаёт его и выполняет init.sql из classpath.
 */
public class DBConnection {

    private static String url;
    private static String dbFilePath;
    private static String initScript;
    private static volatile boolean initialized = false;

    static {
        try (InputStream input = DBConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {

            Properties props = new Properties();
            if (input == null) {
                throw new RuntimeException("Не найден файл db.properties");
            }
            props.load(input);

            Class.forName(props.getProperty("db.driver"));
            url = props.getProperty("db.url");
            initScript = props.getProperty("db.init.script", "init.sql");

            // Извлекаем путь к файлу БД из URL вида "jdbc:sqlite:car_rental.db"
            if (url != null && url.startsWith("jdbc:sqlite:")) {
                dbFilePath = url.substring("jdbc:sqlite:".length());
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка инициализации подключения к БД", e);
        }
    }

    /**
     * Получить новое соединение с БД.
     * При первом обращении автоматически создаёт схему из init.sql,
     * если файл БД ещё не существует.
     */
    public static Connection getConnection() throws SQLException {
        ensureDatabaseInitialized();
        Connection con = DriverManager.getConnection(url);
        // Включаем поддержку внешних ключей (по умолчанию в SQLite выключена)
        try (Statement st = con.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return con;
    }

    /**
     * Проверка и инициализация БД при первом запуске.
     */
    private static synchronized void ensureDatabaseInitialized() {
        if (initialized) return;

        boolean needInit = false;
        if (dbFilePath != null) {
            File dbFile = new File(dbFilePath);
            if (!dbFile.exists() || dbFile.length() == 0) {
                needInit = true;
                System.out.println("[DB] Файл БД не найден, инициализация: " + dbFile.getAbsolutePath());
            }
        }

        if (needInit) {
            try (Connection con = DriverManager.getConnection(url)) {
                String sql = loadResource(initScript);
                if (sql != null && !sql.isEmpty()) {
                    executeSqlScript(con, sql);
                    System.out.println("[DB] Инициализация БД завершена успешно.");
                }
            } catch (SQLException | IOException e) {
                throw new RuntimeException("Ошибка инициализации БД из " + initScript, e);
            }
        }
        initialized = true;
    }

    /**
     * Загрузить SQL-скрипт из classpath.
     */
    private static String loadResource(String resourcePath) throws IOException {
        try (InputStream is = DBConnection.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) return null;
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            }
            return sb.toString();
        }
    }

    /**
     * Выполнить SQL-скрипт построчно (по разделителю «;»).
     * Простой парсер: учитывает многострочные комментарии «--» в начале строки.
     */
    private static void executeSqlScript(Connection con, String script) throws SQLException {
        // Удаляем строки-комментарии, начинающиеся с "--"
        StringBuilder cleaned = new StringBuilder();
        for (String raw : script.split("\n")) {
            String line = raw.trim();
            if (line.startsWith("--") || line.isEmpty()) continue;
            cleaned.append(raw).append('\n');
        }

        try (Statement st = con.createStatement()) {
            for (String statement : cleaned.toString().split(";")) {
                String s = statement.trim();
                if (!s.isEmpty()) {
                    st.execute(s);
                }
            }
        }
    }
}
