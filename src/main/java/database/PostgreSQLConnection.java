package main.java.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgreSQLConnection {
    // Nilai default. Isi sebenarnya dibaca dari file db.properties (tidak ikut di-commit ke GitHub,
    // lihat .gitignore). Salin db.properties.example -> db.properties lalu isi user & password sendiri.
    private static String url = "jdbc:postgresql://localhost:5432/db_bsdmart_pln";
    private static String user = "Dave Joseph";
    private static String password = "D4ve.0521";

    static {
        try (FileInputStream in = new FileInputStream("db.properties")) {
            Properties p = new Properties();
            p.load(in);
            url = p.getProperty("db.url", url);
            user = p.getProperty("db.user", user);
            password = p.getProperty("db.password", password);
        } catch (IOException e) {
            System.err.println("db.properties tidak ditemukan di folder project. "
                    + "Salin db.properties.example menjadi db.properties lalu isi user dan password PostgreSQL.");
        }
    }

    public static Connection getConnection() throws SQLException {
        // Daftarkan driver secara eksplisit (project ini tanpa build tool)
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver jar missing from lib/ folder", e);
        }
        return DriverManager.getConnection(url, user, password);
    }
}
