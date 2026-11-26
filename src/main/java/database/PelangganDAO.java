package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import main.java.model.Pelanggan;

public class PelangganDAO {

    public List<Pelanggan> getAllPelanggan() throws SQLException {
        List<Pelanggan> list = new ArrayList<>();
        String sql = "SELECT id_pelanggan, nama FROM pelanggan ORDER BY id_pelanggan";
        try (Connection conn = PostgreSQLConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Pelanggan(rs.getString("id_pelanggan"), rs.getString("nama")));
            }
        }
        return list;
    }

    public void savePelanggan(Pelanggan p) throws SQLException {
        String sql = "INSERT INTO pelanggan (id_pelanggan, nama) VALUES (?, ?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getIdPelanggan());
            ps.setString(2, p.getNama());
            ps.executeUpdate();
        }
    }

    public void updatePelanggan(Pelanggan p) throws SQLException {
        String sql = "UPDATE pelanggan SET nama = ? WHERE id_pelanggan = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNama());
            ps.setString(2, p.getIdPelanggan());
            ps.executeUpdate();
        }
    }

    // Akan gagal (FK) jika pelanggan sudah punya struk -> tampilkan pesan error di controller
    public void deletePelanggan(String idPelanggan) throws SQLException {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM pelanggan WHERE id_pelanggan = ?")) {
            ps.setString(1, idPelanggan);
            ps.executeUpdate();
        }
    }
}
