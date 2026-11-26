package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import main.java.model.PLNPraBayar;

public class PLNPraBayarDAO {

    // Dipanggil di dalam transaction milik StrukDAO (conn dipinjam, TIDAK ditutup di sini)
    public int insert(Connection conn, PLNPraBayar p) throws SQLException {
        String sql = "INSERT INTO pln_prabayar (no_meter, id_pel, nama, tarif_daya, no_ref, token, " +
                     "jumlah_kwh, nominal_token, biaya_admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNoMeter());
            ps.setString(2, p.getIdPel());
            ps.setString(3, p.getNama());
            ps.setString(4, p.getTarifDaya());
            ps.setString(5, p.getNoRef());
            ps.setString(6, p.getToken());
            ps.setDouble(7, p.getJumlahKwh());
            ps.setDouble(8, p.getNominalToken());
            ps.setDouble(9, p.getBiayaAdmin());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setIdPln(keys.getInt(1));
                    return p.getIdPln();
                }
            }
        }
        throw new SQLException("Gagal mendapatkan id_pln");
    }
}
