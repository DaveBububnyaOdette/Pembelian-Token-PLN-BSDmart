package main.java.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;

// Struk implements Cetak (interface) + ASSOCIATION ke Kasir, Pelanggan, PLNPraBayar, Pembayaran
public class Struk implements Cetak {
    private int idStruk = -1; // primary key (ditambahkan, auto-increment di DB)
    private final StringProperty namaToko = new SimpleStringProperty();
    private final StringProperty alamat = new SimpleStringProperty();
    private final StringProperty tanggal = new SimpleStringProperty();
    private final StringProperty waktu = new SimpleStringProperty();
    private final StringProperty noTrx = new SimpleStringProperty();
    private final DoubleProperty totalBayar = new SimpleDoubleProperty();

    // Association
    private Kasir kasir;
    private Pelanggan pelanggan;
    private PLNPraBayar plnPraBayar;
    private Pembayaran pembayaran;

    public Struk() {}

    public Struk(String namaToko, String alamat, String tanggal, String waktu,
                 String noTrx, double totalBayar) {
        setNamaToko(namaToko);
        setAlamat(alamat);
        setTanggal(tanggal);
        setWaktu(waktu);
        setNoTrx(noTrx);
        setTotalBayar(totalBayar);
    }

    public int getIdStruk() { return idStruk; }
    public void setIdStruk(int idStruk) { this.idStruk = idStruk; }

    public StringProperty namaTokoProperty() { return namaToko; }
    public String getNamaToko() { return namaToko.get(); }
    public void setNamaToko(String v) { namaToko.set(v); }

    public StringProperty alamatProperty() { return alamat; }
    public String getAlamat() { return alamat.get(); }
    public void setAlamat(String v) { alamat.set(v); }

    public StringProperty tanggalProperty() { return tanggal; }
    public String getTanggal() { return tanggal.get(); }
    public void setTanggal(String v) { tanggal.set(v); }

    public StringProperty waktuProperty() { return waktu; }
    public String getWaktu() { return waktu.get(); }
    public void setWaktu(String v) { waktu.set(v); }

    public StringProperty noTrxProperty() { return noTrx; }
    public String getNoTrx() { return noTrx.get(); }
    public void setNoTrx(String v) { noTrx.set(v); }

    public DoubleProperty totalBayarProperty() { return totalBayar; }
    public double getTotalBayar() { return totalBayar.get(); }
    public void setTotalBayar(double v) { totalBayar.set(v); }

    public Kasir getKasir() { return kasir; }
    public void setKasir(Kasir kasir) { this.kasir = kasir; }
    public Pelanggan getPelanggan() { return pelanggan; }
    public void setPelanggan(Pelanggan pelanggan) { this.pelanggan = pelanggan; }
    public PLNPraBayar getPlnPraBayar() { return plnPraBayar; }
    public void setPlnPraBayar(PLNPraBayar p) { this.plnPraBayar = p; }
    public Pembayaran getPembayaran() { return pembayaran; }
    public void setPembayaran(Pembayaran p) { this.pembayaran = p; }

    // Properti tambahan untuk kolom TableView (menampilkan data dari object terkait)
    public String getNamaKasir() { return kasir == null ? "" : kasir.getNama(); }
    public String getNamaPelanggan() { return pelanggan == null ? "" : pelanggan.getNama(); }
    public String getJenisPembayaran() { return pembayaran == null ? "" : pembayaran.getJenis(); }

    /** Isi teks struk, dipakai oleh struk() dan bisa dipakai controller untuk WebView/TextArea. */
    public String buildStrukText() {
        StringBuilder sb = new StringBuilder();
        sb.append(getNamaToko()).append("\n").append(getAlamat()).append("\n");
        sb.append("--------------------------------\n");
        sb.append("Tgl/Waktu : ").append(getTanggal()).append(" ").append(getWaktu()).append("\n");
        sb.append("No. Trx   : ").append(getNoTrx()).append("\n");
        if (kasir != null) sb.append("Kasir     : ").append(kasir.getNama()).append("\n");
        if (pelanggan != null) sb.append("Pelanggan : ").append(pelanggan.getNama()).append("\n");
        sb.append("--------------------------------\n");
        sb.append("STRUK PEMBELIAN LISTRIK PRABAYAR\n");
        if (plnPraBayar != null) {
            PLNPraBayar p = plnPraBayar;
            sb.append("No. Meter : ").append(p.getNoMeter()).append("\n");
            sb.append("ID Pel    : ").append(p.getIdPel()).append("\n");
            sb.append("Nama      : ").append(p.getNama()).append("\n");
            sb.append("Tarif/Daya: ").append(p.getTarifDaya()).append("\n");
            sb.append("No. Ref   : ").append(p.getNoRef()).append("\n");
            sb.append(String.format("Nominal   : Rp %,.0f%n", p.getNominalToken()));
            sb.append(String.format("Admin     : Rp %,.0f%n", p.getBiayaAdmin()));
            sb.append(String.format("Jml kWh   : %.2f%n", p.getJumlahKwh()));
            sb.append("TOKEN     : ").append(p.getToken()).append("\n");
        }
        sb.append("--------------------------------\n");
        sb.append(String.format("TOTAL     : Rp %,.0f%n", getTotalBayar()));
        if (pembayaran instanceof PembayaranTunai) {
            PembayaranTunai t = (PembayaranTunai) pembayaran;
            sb.append("Metode    : Tunai\n");
            sb.append(String.format("Dibayar   : Rp %,.0f%n", t.getUangDibayar()));
            sb.append(String.format("Kembalian : Rp %,.0f%n", t.getKembalian()));
        } else if (pembayaran instanceof PembayaranQris) {
            sb.append("Metode    : QRIS\n");
            sb.append("Ref QRIS  : ").append(((PembayaranQris) pembayaran).getNomorReferensi()).append("\n");
        }
        sb.append("--------------------------------\n");
        sb.append("Terima kasih telah berbelanja\n");
        return sb.toString();
    }

    // Implementasi method interface Cetak
    @Override
    public void struk() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Struk");
        alert.setHeaderText("Struk " + getNoTrx());
        alert.setContentText(buildStrukText());
        alert.showAndWait();
    }
}
