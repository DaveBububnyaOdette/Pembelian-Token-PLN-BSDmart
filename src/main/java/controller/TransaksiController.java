package main.java.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.java.database.PelangganDAO;
import main.java.database.StrukDAO;
import main.java.model.*;

public class TransaksiController {
    private static final String NAMA_TOKO = "BSDmart Tarumanagara";
    private static final String ALAMAT_TOKO = "Jl. Letjen S. Parman No.1, Jakarta Barat";
    private static final double BIAYA_ADMIN = 2500;

    @FXML private Label lblKasir, lblAdmin, lblKwh, lblTotal;
    @FXML private ComboBox<Pelanggan> pelangganCombo;
    @FXML private TextField noMeterField, idPelField, namaPlnField;
    @FXML private ComboBox<String> tarifCombo, metodeCombo;
    @FXML private ComboBox<Integer> nominalCombo;
    @FXML private VBox dynamicBayarContainer;

    // Field dinamis (dibuat lewat kode, seperti form Seller/Buyer di contoh dosen)
    private TextField uangDibayarField;
    private TextField noRefQrisField;
    private final Label lblKembalian = new Label();

    private final PelangganDAO pelangganDAO = new PelangganDAO();
    private final StrukDAO strukDAO = new StrukDAO();
    private final Map<String, Double> tarifPerKwh = new LinkedHashMap<>();

    @FXML
    public void initialize() {
        // Tarif per kWh (simulasi, mengacu tarif PLN)
        tarifPerKwh.put("R1/450VA", 415.0);
        tarifPerKwh.put("R1/900VA", 1352.0);
        tarifPerKwh.put("R1/1300VA", 1444.70);
        tarifPerKwh.put("R1/2200VA", 1444.70);

        tarifCombo.setItems(FXCollections.observableArrayList(tarifPerKwh.keySet()));
        nominalCombo.setItems(FXCollections.observableArrayList(20000, 50000, 100000, 200000, 500000, 1000000));
        metodeCombo.setItems(FXCollections.observableArrayList("Tunai", "QRIS"));
        metodeCombo.setValue("Tunai");

        lblAdmin.setText(String.format("Rp %,.0f", BIAYA_ADMIN));
        if (Session.kasirAktif != null) {
            lblKasir.setText("Kasir: " + Session.kasirAktif.getNama() + " (" + Session.kasirAktif.getIdKasir() + ")");
        }
        loadPelanggan();
        buildBayarForm();
        hitungRingkasan();
    }

    @FXML
    private void handleRefreshPelanggan() { loadPelanggan(); }

    private void loadPelanggan() {
        try {
            pelangganCombo.setItems(FXCollections.observableArrayList(pelangganDAO.getAllPelanggan()));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal memuat pelanggan: " + e.getMessage());
        }
    }

    @FXML private void handleHitung() { hitungRingkasan(); }
    @FXML private void handleMetode() { buildBayarForm(); }

    // ------------------------------------------------------------------
    private double hitungKwh() {
        Integer nominal = nominalCombo.getValue();
        String tarif = tarifCombo.getValue();
        if (nominal == null || tarif == null) return 0;
        return Math.round(nominal / tarifPerKwh.get(tarif) * 100.0) / 100.0;
    }

    private double hitungTotal() {
        Integer nominal = nominalCombo.getValue();
        return (nominal == null ? 0 : nominal) + BIAYA_ADMIN;
    }

    private void hitungRingkasan() {
        lblKwh.setText(String.format("%.2f kWh", hitungKwh()));
        lblTotal.setText(String.format("Rp %,.0f", hitungTotal()));
        updateKembalian();
    }

    // Form pembayaran berubah sesuai metode yang dipilih
    private void buildBayarForm() {
        dynamicBayarContainer.getChildren().clear();
        uangDibayarField = null;
        noRefQrisField = null;

        if ("Tunai".equals(metodeCombo.getValue())) {
            uangDibayarField = new TextField();
            uangDibayarField.setPromptText("contoh: 110000");
            uangDibayarField.textProperty().addListener((obs, o, n) -> updateKembalian());
            Label l = new Label("Uang Dibayar (Rp):");
            l.setMinWidth(140);
            dynamicBayarContainer.getChildren().addAll(new HBox(10, l, uangDibayarField), lblKembalian);
        } else {
            noRefQrisField = new TextField();
            noRefQrisField.setPromptText("contoh: QR20261124001");
            Label l = new Label("No. Referensi QRIS:");
            l.setMinWidth(140);
            dynamicBayarContainer.getChildren().add(new HBox(10, l, noRefQrisField));
        }
        updateKembalian();
    }

    private void updateKembalian() {
        if (uangDibayarField == null) return;
        double uang = parseDouble(uangDibayarField.getText());
        if (uang < 0) {
            lblKembalian.setText("Kembalian: -");
        } else {
            lblKembalian.setText(String.format("Kembalian: Rp %,.0f", uang - hitungTotal()));
        }
    }

    private double parseDouble(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return -1;
        }
    }

    // ------------------------------------------------------------------
    @FXML
    private void handleBayar() {
        // ---- validasi input ----
        Pelanggan pelanggan = pelangganCombo.getValue();
        if (pelanggan == null) { warn("Pilih pelanggan terlebih dahulu."); return; }

        String noMeter = noMeterField.getText().trim();
        if (!noMeter.matches("\\d{11,12}")) { warn("No. meter harus berupa 11-12 digit angka."); return; }

        String idPel = idPelField.getText().trim();
        String namaPln = namaPlnField.getText().trim();
        if (idPel.isEmpty() || namaPln.isEmpty()) { warn("ID Pelanggan PLN dan Nama wajib diisi."); return; }

        if (tarifCombo.getValue() == null) { warn("Pilih tarif/daya."); return; }
        if (nominalCombo.getValue() == null) { warn("Pilih nominal token."); return; }
        if (Session.kasirAktif == null) { warn("Kasir belum login."); return; }

        double total = hitungTotal();
        String tanggal = LocalDate.now().toString();
        String waktu = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        // ---- buat object Pembayaran (POLYMORPHISM: tipe Pembayaran, isi Tunai / QRIS) ----
        Pembayaran pembayaran;
        if ("Tunai".equals(metodeCombo.getValue())) {
            double uang = parseDouble(uangDibayarField.getText());
            if (uang < 0) { warn("Uang dibayar harus berupa angka."); return; }
            pembayaran = new PembayaranTunai(total, tanggal, waktu, uang);
        } else {
            pembayaran = new PembayaranQris(total, tanggal, waktu, noRefQrisField.getText().trim());
        }

        try {
            pembayaran.prosesPembayaran(); // method abstract -> perilaku beda tiap subclass
        } catch (IllegalStateException e) {
            warn(e.getMessage());
            return;
        }

        // ---- buat data PLN ----
        PLNPraBayar pln = new PLNPraBayar(noMeter, idPel, namaPln, tarifCombo.getValue(),
                generateNoRef(), generateToken(), hitungKwh(), nominalCombo.getValue(), BIAYA_ADMIN);

        // ---- buat Struk (ASSOCIATION ke 4 class) ----
        Struk struk = new Struk(NAMA_TOKO, ALAMAT_TOKO, tanggal, waktu, strukDAO.generateNoTrx(), total);
        struk.setKasir(Session.kasirAktif);
        struk.setPelanggan(pelanggan);
        struk.setPlnPraBayar(pln);
        struk.setPembayaran(pembayaran);

        try {
            strukDAO.saveTransaksi(struk);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal menyimpan transaksi:\n" + e.getMessage());
            return;
        }

        struk.struk(); // method interface Cetak -> tampilkan struk
        handleReset();
    }

    @FXML
    private void handleReset() {
        pelangganCombo.setValue(null);
        noMeterField.clear();
        idPelField.clear();
        namaPlnField.clear();
        tarifCombo.setValue(null);
        nominalCombo.setValue(null);
        metodeCombo.setValue("Tunai");
        buildBayarForm();
        hitungRingkasan();
    }

    private String generateToken() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            if (i > 0 && i % 4 == 0) sb.append('-');
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }

    private String generateNoRef() {
        return "REF" + (100000000L + (long) (new Random().nextDouble() * 899999999L));
    }

    private void warn(String msg) { showAlert(Alert.AlertType.WARNING, msg); }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
