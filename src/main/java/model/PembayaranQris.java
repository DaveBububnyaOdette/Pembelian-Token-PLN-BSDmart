package main.java.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PembayaranQris extends Pembayaran {
    private final StringProperty nomorReferensi = new SimpleStringProperty();

    public PembayaranQris() { setJenis("QRIS"); }

    public PembayaranQris(double jumlah, String tanggal, String waktu, String nomorReferensi) {
        super(jumlah, tanggal, waktu);
        setJenis("QRIS");
        setNomorReferensi(nomorReferensi);
    }

    public StringProperty nomorReferensiProperty() { return nomorReferensi; }
    public String getNomorReferensi() { return nomorReferensi.get(); }
    public void setNomorReferensi(String v) { nomorReferensi.set(v); }

    @Override
    public void prosesPembayaran() {
        if (getNomorReferensi() == null || getNomorReferensi().trim().isEmpty()) {
            throw new IllegalStateException("Nomor referensi QRIS wajib diisi!");
        }
        // QRIS dianggap langsung lunas, tidak ada kembalian
    }
}
