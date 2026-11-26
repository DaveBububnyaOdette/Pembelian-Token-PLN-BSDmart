package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import main.java.model.Kasir;

public class KasirDAO {

    // LOGIN: kembalikan Kasir jika username & password cocok, null jika tidak
    // (PreparedStatement -> aman dari SQL injection; password dicek dengan BCrypt)
    public Kasir login(String username, String password) throws SQLException {
        String sql = "SELECT id_kasir, nama, username, password FROM kasir WHERE username = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && BCrypt.checkpw(password, rs.getString("password"))) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public List<Kasir> getAllKasir() throws SQLException {
        List<Kasir> list = new ArrayList<>();
        String sql = "SELECT id_kasir, nama, username, password FROM kasir ORDER BY id_kasir";
        try (Connection conn = PostgreSQLConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // CREATE: password di-hash dulu, yang masuk database adalah hash-nya
    public void saveKasir(Kasir k) throws SQLException {
        String sql = "INSERT INTO kasir (id_kasir, nama, username, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, k.getIdKasir());
            ps.setString(2, k.getNama());
            ps.setString(3, k.getUsername());
            ps.setString(4, BCrypt.hashpw(k.getPassword(), BCrypt.gensalt(12)));
            ps.executeUpdate();
        }
    }

    // UPDATE: ubahPassword = false -> password lama di database tidak disentuh
    public void updateKasir(Kasir k, boolean ubahPassword) throws SQLException {
        String sql = ubahPassword
                ? "UPDATE kasir SET nama = ?, username = ?, password = ? WHERE id_kasir = ?"
                : "UPDATE kasir SET nama = ?, username = ? WHERE id_kasir = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, k.getNama());
            ps.setString(2, k.getUsername());
            if (ubahPassword) {
                ps.setString(3, BCrypt.hashpw(k.getPassword(), BCrypt.gensalt(12)));
                ps.setString(4, k.getIdKasir());
            } else {
                ps.setString(3, k.getIdKasir());
            }
            ps.executeUpdate();
        }
    }

    // Gagal (FK) jika kasir sudah pernah membuat struk
    public void deleteKasir(String idKasir) throws SQLException {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM kasir WHERE id_kasir = ?")) {
            ps.setString(1, idKasir);
            ps.executeUpdate();
        }
    }

    private Kasir map(ResultSet rs) throws SQLException {
        return new Kasir(rs.getString("id_kasir"), rs.getString("nama"),
                         rs.getString("username"), rs.getString("password"));
    }
}
