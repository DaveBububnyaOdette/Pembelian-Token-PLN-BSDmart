package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import main.java.model.*;

public class StrukDAO {
    private final PLNPraBayarDAO plnDAO = new PLNPraBayarDAO();
    private final PembayaranDAO pembayaranDAO = new PembayaranDAO();

    // Satu SELECT dengan JOIN semua tabel -> langsung jadi object Struk lengkap.
    // Semua kolom di-alias supaya tidak bentrok (nama, tanggal, waktu ada di banyak tabel).
    private static final String SELECT_STRUK =
        "SELECT s.id_struk, s.no_trx, s.nama_toko, s.alamat, s.tanggal AS s_tanggal, " +
        "       s.waktu AS s_waktu, s.total_bayar, " +
        "       k.id_kasir, k.nama AS k_nama, k.username AS k_username, k.password AS k_password, " +
        "       pl.id_pelanggan, pl.nama AS pl_nama, " +
        "       n.id_pln, n.no_meter, n.id_pel, n.nama AS n_nama, n.tarif_daya, n.no_ref, n.token, " +
        "       n.jumlah_kwh, n.nominal_token, n.biaya_admin, " +
        "       pb.id_pembayaran, pb.jenis, pb.jumlah, pb.tanggal AS pb_tanggal, pb.waktu AS pb_waktu, " +
        "       pt.uang_dibayar, pt.kembalian, pq.nomor_referensi " +
        "FROM struk s " +
        "JOIN kasir k ON s.id_kasir = k.id_kasir " +
        "JOIN pelanggan pl ON s.id_pelanggan = pl.id_pelanggan " +
        "JOIN pln_prabayar n ON s.id_pln = n.id_pln " +
        "JOIN pembayaran pb ON s.id_pembayaran = pb.id_pembayaran " +
        "LEFT JOIN pembayaran_tunai pt ON pb.id_pembayaran = pt.id_pembayaran " +
        "LEFT JOIN pembayaran_qris pq ON pb.id_pembayaran = pq.id_pembayaran ";

    // Nomor transaksi unik berbasis waktu, contoh: TRX20261124103015
    public String generateNoTrx() {
        return "TRX" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    // CREATE: simpan 1 transaksi penuh dalam SATU transaction
    // (pln_prabayar -> pembayaran(+child) -> struk). Kalau salah satu gagal -> rollback semua.
    public void saveTransaksi(Struk s) throws SQLException {
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int idPln = plnDAO.insert(conn, s.getPlnPraBayar());
                int idPembayaran = pembayaranDAO.insert(conn, s.getPembayaran());

                String sql = "INSERT INTO struk (no_trx, nama_toko, alamat, tanggal, waktu, total_bayar, " +
                             "id_kasir, id_pelanggan, id_pln, id_pembayaran) VALUES (?,?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, s.getNoTrx());
                    ps.setString(2, s.getNamaToko());
                    ps.setString(3, s.getAlamat());
                    ps.setString(4, s.getTanggal());
                    ps.setString(5, s.getWaktu());
                    ps.setDouble(6, s.getTotalBayar());
                    ps.setString(7, s.getKasir().getIdKasir());
                    ps.setString(8, s.getPelanggan().getIdPelanggan());
                    ps.setInt(9, idPln);
                    ps.setInt(10, idPembayaran);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) s.setIdStruk(keys.getInt(1));
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // RETRIEVE semua struk (terbaru di atas)
    public List<Struk> getAllStruk() throws SQLException {
        List<Struk> list = new ArrayList<>();
        String sql = SELECT_STRUK + "ORDER BY s.id_struk DESC";
        try (Connection conn = PostgreSQLConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // DELETE: hapus struk + data PLN + data pembayaran yang terkait (child tunai/qris ikut CASCADE)
    public void deleteStruk(Struk s) throws SQLException {
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                exec(conn, "DELETE FROM struk WHERE id_struk = ?", s.getIdStruk());
                exec(conn, "DELETE FROM pln_prabayar WHERE id_pln = ?", s.getPlnPraBayar().getIdPln());
                exec(conn, "DELETE FROM pembayaran WHERE id_pembayaran = ?", s.getPembayaran().getIdPembayaran());
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private void exec(Connection conn, String sql, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Ubah 1 baris hasil JOIN menjadi object Struk lengkap (polymorphism: Tunai / QRIS)
    private Struk map(ResultSet rs) throws SQLException {
        Struk s = new Struk(rs.getString("nama_toko"), rs.getString("alamat"),
                rs.getString("s_tanggal"), rs.getString("s_waktu"),
                rs.getString("no_trx"), rs.getDouble("total_bayar"));
        s.setIdStruk(rs.getInt("id_struk"));

        s.setKasir(new Kasir(rs.getString("id_kasir"), rs.getString("k_nama"),
                rs.getString("k_username"), rs.getString("k_password")));
        s.setPelanggan(new Pelanggan(rs.getString("id_pelanggan"), rs.getString("pl_nama")));

        PLNPraBayar n = new PLNPraBayar(rs.getString("no_meter"), rs.getString("id_pel"),
                rs.getString("n_nama"), rs.getString("tarif_daya"), rs.getString("no_ref"),
                rs.getString("token"), rs.getDouble("jumlah_kwh"),
                rs.getDouble("nominal_token"), rs.getDouble("biaya_admin"));
        n.setIdPln(rs.getInt("id_pln"));
        s.setPlnPraBayar(n);

        Pembayaran pb;
        if ("Tunai".equals(rs.getString("jenis"))) {
            PembayaranTunai t = new PembayaranTunai(rs.getDouble("jumlah"),
                    rs.getString("pb_tanggal"), rs.getString("pb_waktu"), rs.getDouble("uang_dibayar"));
            t.setKembalian(rs.getDouble("kembalian"));
            pb = t;
        } else {
            pb = new PembayaranQris(rs.getDouble("jumlah"),
                    rs.getString("pb_tanggal"), rs.getString("pb_waktu"), rs.getString("nomor_referensi"));
        }
        pb.setIdPembayaran(rs.getInt("id_pembayaran"));
        s.setPembayaran(pb);
        return s;
    }
}
