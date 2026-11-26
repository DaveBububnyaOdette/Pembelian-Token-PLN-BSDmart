package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import main.java.model.Pembayaran;
import main.java.model.PembayaranQris;
import main.java.model.PembayaranTunai;

// Pola sama dengan PersonDAO: insert tabel parent dulu -> ambil generated key -> insert tabel child
public class PembayaranDAO {

    // Dipanggil di dalam transaction milik StrukDAO
    public int insert(Connection conn, Pembayaran pb) throws SQLException {
        String sql = "INSERT INTO pembayaran (jenis, jumlah, tanggal, waktu) VALUES (?, ?, ?, ?)";
        int id;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pb.getJenis());
            ps.setDouble(2, pb.getJumlah());
            ps.setString(3, pb.getTanggal());
            ps.setString(4, pb.getWaktu());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Gagal mendapatkan id_pembayaran");
                id = keys.getInt(1);
            }
        }
        pb.setIdPembayaran(id);

        if (pb instanceof PembayaranTunai) {
            PembayaranTunai t = (PembayaranTunai) pb;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO pembayaran_tunai (id_pembayaran, uang_dibayar, kembalian) VALUES (?, ?, ?)")) {
                ps.setInt(1, id);
                ps.setDouble(2, t.getUangDibayar());
                ps.setDouble(3, t.getKembalian());
                ps.executeUpdate();
            }
        } else if (pb instanceof PembayaranQris) {
            PembayaranQris q = (PembayaranQris) pb;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO pembayaran_qris (id_pembayaran, nomor_referensi) VALUES (?, ?)")) {
                ps.setInt(1, id);
                ps.setString(2, q.getNomorReferensi());
                ps.executeUpdate();
            }
        }
        return id;
    }
}
